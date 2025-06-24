package com.priyanshparekh.fairshare.model

import com.priyanshparekh.fairshare.group.balances.BalancesAdapter

data class BalanceInfoItem(
    val userId: Long?,
    val username: String?,
    val amount: Double?,
    val headerText: String?,
    val buttonText: String?,
    val viewType: BalancesAdapter.ItemViewType
)