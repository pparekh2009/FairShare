package com.priyanshparekh.fairshareapi.exportdata;

import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfo;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExportDataDTO {

    private List<Expense> expenseList;
    private List<BalanceInfo> balanceInfoList;
    private List<User> userList;

}
