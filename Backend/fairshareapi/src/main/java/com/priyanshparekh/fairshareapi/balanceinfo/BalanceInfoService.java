package com.priyanshparekh.fairshareapi.balanceinfo;

import com.priyanshparekh.fairshareapi.expense.ExpenseDTO;
import com.priyanshparekh.fairshareapi.user.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BalanceInfoService {

    private final BalanceInfoRepository balanceInfoRepository;

    public BalanceInfoService(BalanceInfoRepository balanceInfoRepository) {
        this.balanceInfoRepository = balanceInfoRepository;
    }

    public void addBalanceInfo(ExpenseDTO expense) {
        List<User> splitBetweenUsers = expense.getSplitBetween();
        Double individualAmount = expense.getAmount() / splitBetweenUsers.size();

        for (User user : splitBetweenUsers) {
            if (!user.getId().equals(expense.getPaidBy())) {

                // Owes To

//                addOwesToEntry(expense, user, individualAmount);
                addEntry(expense.getGroupId(), user.getId(), BalanceInfo.Direction.OWES_TO, expense.getPaidBy(), individualAmount);



                // Receive From

//                addReceivesFromEntry(expense, user, individualAmount);
                addEntry(expense.getGroupId(), expense.getPaidBy(), BalanceInfo.Direction.RECEIVES_FROM, user.getId(), individualAmount);
            }
        }
    }

    private void addEntry(Long groupId, Long userId, BalanceInfo.Direction direction, Long otherUserId, Double amount) {
        BalanceInfo receiveBalanceInfo = BalanceInfo.builder()
                .groupId(groupId)
                .userId(userId)
                .direction(direction)
                .otherUserId(otherUserId)
                .amount(amount)
                .build();

        BalanceInfo newReceiveBalanceInfo = balanceInfoRepository.findByGroupIdAndUserIdAndOtherUserId(groupId, userId, otherUserId)
                .orElse(receiveBalanceInfo);

        Double newReceiveAmount = newReceiveBalanceInfo.getAmount() + amount;
        newReceiveBalanceInfo.setAmount(newReceiveAmount);
        balanceInfoRepository.save(newReceiveBalanceInfo);
    }

    private void addReceivesFromEntry(ExpenseDTO expense, User user, Double individualAmount) {
        BalanceInfo receiveBalanceInfo = BalanceInfo.builder()
                .groupId(expense.getGroupId())
                .userId(expense.getPaidBy())
                .direction(BalanceInfo.Direction.RECEIVES_FROM)
                .otherUserId(user.getId())
                .amount(individualAmount)
                .build();

        BalanceInfo newReceiveBalanceInfo = balanceInfoRepository.findByGroupIdAndUserIdAndOtherUserId(expense.getGroupId(), user.getId(), expense.getPaidBy())
                .orElse(receiveBalanceInfo);

        Double newReceiveAmount = newReceiveBalanceInfo.getAmount() + individualAmount;
        newReceiveBalanceInfo.setAmount(newReceiveAmount);
        balanceInfoRepository.save(newReceiveBalanceInfo);
    }

    private void addOwesToEntry(ExpenseDTO expense, User user, Double individualAmount) {
        BalanceInfo oweBalanceInfo = BalanceInfo.builder()
                .groupId(expense.getGroupId())
                .userId(user.getId())
                .direction(BalanceInfo.Direction.OWES_TO)
                .otherUserId(expense.getPaidBy())
                .amount(individualAmount)
                .build();

        BalanceInfo newOweBalanceInfo = balanceInfoRepository.findByGroupIdAndUserIdAndOtherUserId(expense.getGroupId(), user.getId(), expense.getPaidBy())
                .orElse(oweBalanceInfo);

        Double newOweAmount = newOweBalanceInfo.getAmount() + individualAmount;
        newOweBalanceInfo.setAmount(newOweAmount);
        balanceInfoRepository.save(newOweBalanceInfo);
    }

    public List<BalanceInfo> getBalanceInfo(Long groupId, Long userId) {
        return balanceInfoRepository.findAllByGroupIdAndUserId(groupId, userId);
    }

    public List<BalanceInfo> updateBalanceInfo(List<BalanceInfo> newOwesToList, Long groupId) {
        balanceInfoRepository.deleteAllByGroupId(groupId);

        List<BalanceInfo> newReceivesFromList = newOwesToList.stream().map(balanceInfo -> {
            return new BalanceInfo(null, balanceInfo.getGroupId(), balanceInfo.getOtherUserId(), BalanceInfo.Direction.RECEIVES_FROM, balanceInfo.getUserId(), balanceInfo.getAmount());
        }).toList();

        List<BalanceInfo> newBalanceInfoList = new ArrayList<>();

        newBalanceInfoList.addAll(newOwesToList);
        newBalanceInfoList.addAll(newReceivesFromList);

        return balanceInfoRepository.saveAll(newBalanceInfoList);
    }

    public PayData payUser(PayData payData, Long groupId) {
        BalanceInfo payerData = balanceInfoRepository.findByGroupIdAndUserIdAndOtherUserIdAndDirection(groupId, payData.getPayerId(), payData.getPayeeId(), BalanceInfo.Direction.OWES_TO);
        BalanceInfo payeeData = balanceInfoRepository.findByGroupIdAndUserIdAndOtherUserIdAndDirection(groupId, payData.getPayeeId(), payData.getPayerId(), BalanceInfo.Direction.RECEIVES_FROM);
        double newAmount = payerData.getAmount() - payData.getAmount();

        if (newAmount <= 0.0) {
            balanceInfoRepository.delete(payerData);
            balanceInfoRepository.delete(payeeData);
            return payData;
        }

        payerData.setAmount(newAmount);
        payeeData.setAmount(newAmount);

        balanceInfoRepository.save(payerData);
        balanceInfoRepository.save(payeeData);

        return payData;
    }
}
