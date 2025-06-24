package com.priyanshparekh.fairshare.model

data class BalanceInfo(
    val id: Long?,
    val groupId: Long,
    val userId: Long,
    val direction: Direction,
    val otherUserId: Long,
    val amount: Double
)

enum class Direction {
    OWES_TO,
    RECEIVES_FROM
}

