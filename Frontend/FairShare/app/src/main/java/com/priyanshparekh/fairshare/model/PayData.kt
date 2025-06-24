package com.priyanshparekh.fairshare.model

data class PayData(
    val payerId: Long,
    val payeeId: Long,
    val amount: Double,
    val note: String
)