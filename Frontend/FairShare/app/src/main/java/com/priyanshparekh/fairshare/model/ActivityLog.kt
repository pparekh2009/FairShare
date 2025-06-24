package com.priyanshparekh.fairshare.model

data class ActivityLog(
    val id: Long? = null,
    val groupId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val log: String
)