package com.priyanshparekh.fairshare.auth

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    val name: String,
    val profilePic: String,
    val fcmToken: String
) {}
