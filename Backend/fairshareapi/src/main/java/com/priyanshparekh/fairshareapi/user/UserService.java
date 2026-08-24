package com.priyanshparekh.fairshareapi.user;

import com.priyanshparekh.fairshareapi.notification.UserDevice;
import com.priyanshparekh.fairshareapi.notification.UserDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserService(UserRepository userRepository, UserDeviceRepository userDeviceRepository) {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    UserRequestDTO addUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setName(userRequestDTO.getName());
        byte[] imageBytes = Base64.getDecoder().decode(userRequestDTO.getProfilePic());
        logger.info("UserService: addUser: image bytes: {}", imageBytes);
        user.setProfilePic(imageBytes);

        User savedUser = userRepository.save(user);

        UserRequestDTO userResponseDTO = new UserRequestDTO();
        userResponseDTO.setUsername(savedUser.getUsername());
        userResponseDTO.setEmail(savedUser.getEmail());
        userResponseDTO.setName(savedUser.getName());
        String base64String = Base64.getEncoder().encodeToString(user.getProfilePic());
        userResponseDTO.setProfilePic(base64String);
        return userResponseDTO;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findAllByUsernameStartsWith(query);
    }

    public LoginCompleteDto completeLogin(String username, FcmToken token) {
        User user = getUser(username);

        UserDevice userDevice = new UserDevice();
        userDevice.setUserId(user.getId());
        userDevice.setFcmToken(token.getToken());
        boolean deviceRegistered;
        try {
            userDeviceRepository.save(userDevice);
            deviceRegistered = true;
        } catch (Exception e) {
            log.info("userService: completeLogin: cannot register device");
            deviceRegistered = false;
        }

        return new LoginCompleteDto(user.getId(), user.getUsername(), user.getEmail(), user.getName(), Base64.getEncoder().encodeToString(user.getProfilePic()), deviceRegistered);
    }
}
