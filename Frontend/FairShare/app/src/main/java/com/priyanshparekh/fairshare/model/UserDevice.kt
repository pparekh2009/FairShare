package com.priyanshparekh.fairshare.model

data class UserDevice(
    val id: Long?,
    val userId: Long,
    val fcmToken: String,
    val endpointArn: String?
)
