package com.priyanshparekh.fairshareapi.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    String access_token;
    String refresh_token;
    int expires_in;
    Long id;
    String username;
    String email;
    String name;
    String profilePic;
    boolean deviceRegistered;
}
