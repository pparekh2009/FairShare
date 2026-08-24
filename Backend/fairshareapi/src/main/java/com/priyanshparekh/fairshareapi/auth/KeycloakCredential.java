package com.priyanshparekh.fairshareapi.auth;

public record KeycloakCredential(
        String type,
        String value,
        boolean temporary
) {
}
