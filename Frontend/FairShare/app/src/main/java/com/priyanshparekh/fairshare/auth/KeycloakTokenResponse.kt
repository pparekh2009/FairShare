package com.priyanshparekh.fairshare.auth

data class KeycloakTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int
)
