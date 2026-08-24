package com.priyanshparekh.fairshare.auth

data class LoginCompleteDto(
     val id: Long,
     val username: String,
     val email: String,
     val name: String,
     val profilePic: String,
     val deviceRegistered: Boolean
) {}
