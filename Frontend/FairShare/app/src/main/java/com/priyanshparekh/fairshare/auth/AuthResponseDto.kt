package com.priyanshparekh.fairshare.auth

data class AuthResponseDto(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val id: Long,
    val username: String,
    val email: String,
    val name: String,
    val profilePic: String,
    val deviceRegistered: Boolean
)
