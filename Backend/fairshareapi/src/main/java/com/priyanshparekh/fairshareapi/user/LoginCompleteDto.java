package com.priyanshparekh.fairshareapi.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginCompleteDto {
    Long id;
    String username;
    String email;
    String name;
    String profilePic;
    boolean deviceRegistered;
}
