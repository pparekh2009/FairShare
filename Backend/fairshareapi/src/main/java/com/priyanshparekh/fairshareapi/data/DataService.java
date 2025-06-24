package com.priyanshparekh.fairshareapi.data;

import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfo;
import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfoRepository;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.expense.ExpenseRepository;
import com.priyanshparekh.fairshareapi.expense.UserAmount;
import com.priyanshparekh.fairshareapi.expense.UserAmountRepository;
import com.priyanshparekh.fairshareapi.group.Group;
import com.priyanshparekh.fairshareapi.group.GroupRepository;
import com.priyanshparekh.fairshareapi.groupmember.GroupMember;
import com.priyanshparekh.fairshareapi.groupmember.GroupMemberRepository;
import com.priyanshparekh.fairshareapi.user.User;
import com.priyanshparekh.fairshareapi.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final BalanceInfoRepository balanceInfoRepository;
    private final UserAmountRepository userAmountRepository;
    private final UserRepository userRepository;


    public DataService(ExpenseRepository expenseRepository, GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, BalanceInfoRepository balanceInfoRepository, UserAmountRepository userAmountRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.balanceInfoRepository = balanceInfoRepository;
        this.userAmountRepository = userAmountRepository;
        this.userRepository = userRepository;
    }

    DataDTO getAllData() {
        List<Expense> expenses = expenseRepository.findAll();
        List<Group> groups = groupRepository.findAll();
        List<GroupMember> groupMembers = groupMemberRepository.findAll();
        List<BalanceInfo> balanceInfos = balanceInfoRepository.findAll();
        List<UserAmount> userAmounts = userAmountRepository.findAll();
        List<User> users = userRepository.findAll();

        return new DataDTO(expenses, groups, groupMembers, userAmounts, users, balanceInfos);
    }
}
