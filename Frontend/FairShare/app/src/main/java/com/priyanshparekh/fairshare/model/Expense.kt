package com.priyanshparekh.fairshare.model

data class Expense(
    val id: Long?,
    val groupId: Long?,
    val name: String,
    val amount: Double,
    val note: String,
    val paidBy: Long,
    val splitBetween: List<User>,
    val createdAt: Long,
    var receiptUrl: String?
) {

    override fun toString(): String {
        return "Group id: $groupId, Name: $name, Amount: $amount, Note: $note, Paid By: $paidBy, Split Between: ${splitBetween.size}, Date: $createdAt"
    }
}