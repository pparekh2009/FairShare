package com.priyanshparekh.fairshareapi.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private List<NetBalanceEntry> netBalances;
    private List<DailyExpenseEntry> cumulativeExpenses;
    private List<ExpenseShareEntry> expenseShares;

}
