package com.priyanshparekh.fairshare.group.balances.notification

data class NotificationRequest(
    val userIds: List<Long?>,
    val messageData: Map<String, String>
)
