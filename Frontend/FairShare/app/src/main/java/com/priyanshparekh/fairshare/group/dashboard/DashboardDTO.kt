package com.priyanshparekh.fairshare.group.dashboard

data class DashboardDTO(
    val netBalances: List<NetBalanceEntry>,
    val cumulativeExpenses: List<DailyExpenseEntry>,
    val expenseShares: List<ExpenseShareEntry>
)
