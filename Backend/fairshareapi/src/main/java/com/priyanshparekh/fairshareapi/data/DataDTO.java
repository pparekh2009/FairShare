package com.priyanshparekh.fairshareapi.data;

import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfo;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.expense.UserAmount;
import com.priyanshparekh.fairshareapi.group.Group;
import com.priyanshparekh.fairshareapi.groupmember.GroupMember;
import com.priyanshparekh.fairshareapi.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DataDTO {

    private List<Expense> expenses;
    private List<Group> groups;
    private List<GroupMember> groupMembers;
    private List<UserAmount> userAmounts;
    private List<User> users;
    private List<BalanceInfo> balanceInfos;

}
