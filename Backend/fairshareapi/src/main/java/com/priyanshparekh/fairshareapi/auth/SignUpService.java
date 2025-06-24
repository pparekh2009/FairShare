package com.priyanshparekh.fairshareapi.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

@Service
public class SignUpService {

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    private final CognitoIdentityProviderClient cognitoClient;

    public SignUpService(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public void signUp(SignUpRequestDTO signUpRequestDTO) {
        SignUpRequest signUpRequest = SignUpRequest
                .builder()
                .username(signUpRequestDTO.getUsername())
                .password(signUpRequestDTO.getPassword())
                .userAttributes(
                        AttributeType.builder().name("email").value(signUpRequestDTO.getEmail()).build()
                )
                .clientId(clientId)
                .build();

        cognitoClient.signUp(signUpRequest);
    }
}
