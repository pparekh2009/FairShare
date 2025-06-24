package com.priyanshparekh.fairshareapi.dashboard;

import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfoRepository;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.expense.ExpenseRepository;
import com.priyanshparekh.fairshareapi.expense.UserAmountRepository;
import com.priyanshparekh.fairshareapi.groupmember.GroupMember;
import com.priyanshparekh.fairshareapi.groupmember.GroupMemberRepository;
import com.priyanshparekh.fairshareapi.user.User;
import com.priyanshparekh.fairshareapi.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final UserAmountRepository userAmountRepository;
    private final BalanceInfoRepository balanceInfoRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DashboardService(UserRepository userRepository, GroupMemberRepository groupMemberRepository, ExpenseRepository expenseRepository, UserAmountRepository userAmountRepository, BalanceInfoRepository balanceInfoRepository) {
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.expenseRepository = expenseRepository;
        this.userAmountRepository = userAmountRepository;
        this.balanceInfoRepository = balanceInfoRepository;
    }

    public List<NetBalanceEntry> getNetBalances(Long groupId) {
        List<NetBalanceEntry> result = new ArrayList<>();
        List<Long> userIds = groupMemberRepository.findAllByGroupId(groupId).stream().map(GroupMember::getUserId).toList();
        List<User> users = userRepository.findAllByIdIn(userIds);

        for (User user : users) {
            double paid = expenseRepository.sumPaidByUserInGroup(user.getId(), groupId);
            double owed = userAmountRepository.sumOwedByUserInGroup(user.getId(), groupId);
            result.add(new NetBalanceEntry(user.getId(), paid - owed));
        }

        return result;
    }



    public DashboardDTO getGroupExpenseStats(Long groupId) {
        List<Long> userIds = groupMemberRepository.findAllByGroupId(groupId).stream().map(GroupMember::getUserId).toList();
        List<User> users = userRepository.findAllByIdIn(userIds);

        // 1. Net Balance per Person
//        List<NetBalanceEntry> netBalances = users.stream().map(user -> {
//            double paid = expenseRepository.sumPaidByUserInGroup(user.getId(), groupId);
//            double owed = userAmountRepository.sumOwedByUserInGroup(user.getId(), groupId);
//            return new NetBalanceEntry(user.getName(), paid - owed);
//        }).collect(Collectors.toList());
        List<NetBalanceEntry> netBalances = balanceInfoRepository.findAllNetBalanceByUserIdsAndGroupId(userIds, groupId).stream().map(netBalanceProjection -> {
            return new NetBalanceEntry(netBalanceProjection.getUserId(), netBalanceProjection.getNetBalance());
        }).toList();
        for (NetBalanceEntry netBalanceEntry : netBalances) {
            logger.info("dashBoardService: getGroupExpenseStats: netBalance: {}: {}", netBalanceEntry.getUserId(), netBalanceEntry.getNetBalance());
        }

        // 2. Cumulative Expense Over Time
//        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);
//        Map<Long, Double> dailyTotals = expenses.stream()
//                .collect(Collectors.groupingBy(
//                        Expense::getCreatedAt,
//                        Collectors.summingDouble(Expense::getAmount)
//                ));
//
//        List<DailyExpenseEntry> cumulativeExpenses = new ArrayList<>();
//        double runningTotal = 0;
//        for (Long date : dailyTotals.keySet().stream().sorted().toList()) {
//            runningTotal += dailyTotals.get(date);
//            cumulativeExpenses.add(new DailyExpenseEntry(date, runningTotal));
//        }

        List<DailyExpenseEntry> cumulativeExpenses = expenseRepository.findExpenseByDateAndGroupId(groupId).stream().map(dailyExpenseProjection -> {
            return new DailyExpenseEntry(dailyExpenseProjection.getExpenseDate(), dailyExpenseProjection.getTotalAmount());
        }).toList();
        for (DailyExpenseEntry cumulativeExpense : cumulativeExpenses) {
            logger.info("dashBoardService: getGroupExpenseStats: cumulativeExpense: {}: {}", cumulativeExpense.getExpenseDate(), cumulativeExpense.getTotalAmount());
        }


        // 3. Expense Share Breakdown
//        double groupTotal = expenses.stream().mapToDouble(Expense::getAmount).sum();
//
//        List<ExpenseShareEntry> expenseShares = users.stream().map(user -> {
//            double totalPaid = expenseRepository.sumPaidByUserInGroup(user.getId(), groupId);
//            double percentage = groupTotal == 0 ? 0 : (totalPaid / groupTotal) * 100;
//            return new ExpenseShareEntry(user.getName(), percentage);
//        }).collect(Collectors.toList());
        List<ExpenseShareEntry> expenseShares = expenseRepository.getExpenseShareByGroupId(groupId).stream().map(expenseShareProjection -> new ExpenseShareEntry(expenseShareProjection.getPaidBy(), expenseShareProjection.getUserTotal(), expenseShareProjection.getPercentShare())).toList();
        for (ExpenseShareEntry expenseShare : expenseShares) {
            logger.info("dashBoardService: getGroupExpenseStats: expenseShare: {}: {}", expenseShare.getPaidBy(), expenseShare.getPercentShare());
        }

        return new DashboardDTO(netBalances, cumulativeExpenses, expenseShares);
    }
}
