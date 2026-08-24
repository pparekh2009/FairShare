package com.priyanshparekh.fairshareapi.auth;

public record KeycloakResponseToken(
        String access_token,
        String refresh_token,
        int expires_in
) { }
