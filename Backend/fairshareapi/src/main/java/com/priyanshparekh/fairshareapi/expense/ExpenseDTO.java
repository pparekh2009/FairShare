package com.priyanshparekh.fairshareapi.expense;

import com.priyanshparekh.fairshareapi.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class ExpenseDTO {

    private Long id;
    private Long groupId;
    private String name;
    private Double amount;
    private String note;
    private Long paidBy;
    private Long createdAt;
    private String receiptUrl;
    private List<User> splitBetween;

    public ExpenseDTO() {
    }

    public ExpenseDTO(Long groupId, String name, Double amount, String note, Long paidBy, Long createdAt, String receiptUrl, List<User> splitBetween) {
        this.groupId = groupId;
        this.name = name;
        this.amount = amount;
        this.note = note;
        this.paidBy = paidBy;
        this.createdAt = createdAt;
        this.receiptUrl = receiptUrl;
        this.splitBetween = splitBetween;
    }

    public ExpenseDTO(Long id, Long groupId, String name, Double amount, String note, Long paidBy, Long createdAt, String receiptUrl, List<User> splitBetween) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.amount = amount;
        this.note = note;
        this.paidBy = paidBy;
        this.createdAt = createdAt;
        this.receiptUrl = receiptUrl;
        this.splitBetween = splitBetween;
    }

}
