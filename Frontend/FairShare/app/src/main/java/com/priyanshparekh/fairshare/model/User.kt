package com.priyanshparekh.fairshare.model

data class User(
    val id: Long?,
    val username: String,
    val email: String,
    val name: String,
    val profilePic: String
) {
    constructor(username: String, email: String, name: String, profilePic: String): this(null, username, email, name, profilePic)

    override fun toString(): String {
        return name
    }
}
