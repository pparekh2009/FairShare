package com.priyanshparekh.fairshareapi.auth;

import com.priyanshparekh.fairshareapi.handler.exception.AccountCreatedButLoginFailedException;
import com.priyanshparekh.fairshareapi.handler.exception.AuthServiceException;
import com.priyanshparekh.fairshareapi.handler.exception.UserAlreadyExistsException;
import com.priyanshparekh.fairshareapi.notification.UserDevice;
import com.priyanshparekh.fairshareapi.notification.UserDeviceRepository;
import com.priyanshparekh.fairshareapi.user.User;
import com.priyanshparekh.fairshareapi.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SignUpService {

    @Value("${keycloak.admin.clientId}")
    private String clientId;

    @Value("${keycloak.admin.clientSecret}")
    private String clientSecret;

    @Value("${keycloak.app.clientId}")
    private String appClientId;

    private final RestClient restClient;

    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;

    public SignUpService(
            @Value("${keycloak.baseUrl}")
            String baseUrl, UserRepository userRepository, UserDeviceRepository userDeviceRepository
    ) {
        this.restClient = RestClient.create(baseUrl);
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    public AuthResponseDto signUpAndLogin(SignUpRequestDTO signUpRequestDTO) {
        // Step 2 - Create user in keycloak
        String accessToken = getAdminAccessToken();

        ResponseEntity<Void> response;
        String id = "";
        try {
            response = createUser(signUpRequestDTO, accessToken);

            String path = response.getHeaders().getLocation().getRawPath();
            log.debug("signUpAndLogin: Step 2 successful: path: {}", path);
            id = path.substring(path.lastIndexOf("/") + 1);
            log.debug("signUpAndLogin: Step 2 successful: id: {}", path);
        } catch (HttpClientErrorException.Conflict exception) {
            throw new UserAlreadyExistsException("User with this email already exists!");
        } catch (HttpClientErrorException e) {
            log.error("signUpAndLogin: Step 2 failed: creating Keycloak user: {}", e.getMessage());
            throw new AuthServiceException("Something went wrong while creating your account. Please try again.");
        }

        // Step 3 - Create user in mysql
        User user = new User();
        user.setName(signUpRequestDTO.getName());
        user.setEmail(signUpRequestDTO.getEmail());
        user.setUsername(signUpRequestDTO.getUsername());
        user.setProfilePic(Base64.getDecoder().decode(signUpRequestDTO.getProfilePic()));

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataAccessException exception) {
            log.error("signUpAndLogin: Step 3 failed: saving user to database: {}", exception.getMessage());
            deleteUser(id, accessToken);
            throw new AuthServiceException("Something went wrong while creating your account. Please try again.");
        }

        // Step 4 - Login - Get JWT from Keycloak
        KeycloakResponseToken loginResponse;
        try {
            loginResponse = login(signUpRequestDTO.getEmail(), signUpRequestDTO.getPassword());
        } catch (HttpClientErrorException exception) {
            log.error("signUpAndLogin: Step 4 failed: logging in after signup: {}", exception.getMessage());
            throw new AccountCreatedButLoginFailedException("Your account was created successfully, but we couldn't log you in automatically. Please log in.");
        }

        boolean deviceRegistered;
        try {
            UserDevice userDevice = new UserDevice();
            userDevice.setUserId(savedUser.getId());
            userDevice.setFcmToken(signUpRequestDTO.getFcmToken());
            userDeviceRepository.save(userDevice);
            deviceRegistered = true;
        } catch (Exception exception) {
            log.error("signUpService: signUpAndLogin: exception: {}", exception.getMessage());
            deviceRegistered = false;
        }

        return new AuthResponseDto(
                loginResponse.access_token(), loginResponse.refresh_token(), loginResponse.expires_in(),
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getName(), signUpRequestDTO.getProfilePic(),
                deviceRegistered);
    }

    private void deleteUser(String id, String accessToken) {
        restClient
                .delete()
                .uri("/admin/realms/fairshare/users/{id}", id)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    private ResponseEntity<Void> createUser(SignUpRequestDTO signUpRequestDTO, String accessToken) {
        List<KeycloakCredential> credentials = List.of(new KeycloakCredential("password", signUpRequestDTO.getPassword(), false));

        KeycloakUserRequest userRequest = new KeycloakUserRequest(
                signUpRequestDTO.getUsername(),
                signUpRequestDTO.getEmail(),
                signUpRequestDTO.getName(),
                signUpRequestDTO.getName(),
                true,
                true,
                credentials
        );

        ResponseEntity<Void> response = restClient
                .post()
                .uri("/admin/realms/fairshare/users")
                .header("Authorization", "Bearer " + accessToken)
                .body(userRequest)
                .retrieve()
                .toBodilessEntity();

        return response;
    }

    private String getAdminAccessToken() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList("client_credentials"));
        map.put("client_id", Collections.singletonList(clientId));
        map.put("client_secret", Collections.singletonList(clientSecret));

        KeycloakResponseToken responseToken = restClient
                .post()
                .uri("/realms/fairshare/protocol/openid-connect/token")
                .body(map)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .body(KeycloakResponseToken.class);

        log.debug("signUpService: getAdminAccessToken: access token: {}", responseToken.access_token());

        return responseToken.access_token();
    }

    public KeycloakResponseToken login(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList("password"));
        map.put("client_id", Collections.singletonList(appClientId));
        map.put("username", Collections.singletonList(username));
        map.put("password", Collections.singletonList(password));

        KeycloakResponseToken response = restClient
                .post()
                .uri("/realms/fairshare/protocol/openid-connect/token")
                .body(map)
                .retrieve()
                .body(KeycloakResponseToken.class);

        return response;
    }
}
