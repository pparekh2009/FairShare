package com.priyanshparekh.fairshare.utils

object ActivityLogs {

    // Expense
    fun expenseAdded(memberName: String, expense: String, amount: String) = "$memberName added a new expense \"${expense}\" of \$$amount"

    // Payment
    fun paymentMade(memberName: String, otherMemberName: String, amount: String, note: String?): String = if (note != null) {
        "$memberName payed $otherMemberName $$amount. Note: $note"
    } else {
        "$memberName payed $otherMemberName $$amount"
    }

    // Group
    fun groupCreated(memberName: String, groupName: String) = "$memberName created the group \"$groupName\""

    fun memberAdded(memberName: String, newMemberName: String, groupName: String) = "$memberName added $newMemberName to the group \"$groupName\""
}