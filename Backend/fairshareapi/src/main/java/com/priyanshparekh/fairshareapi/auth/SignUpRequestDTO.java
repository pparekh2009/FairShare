package com.priyanshparekh.fairshareapi.auth;

import lombok.Data;

@Data
public class SignUpRequestDTO {

    private String username;
    private String email;
    private String password;
    private String name;
    private String profilePic;
    private String fcmToken;
}
