package com.priyanshparekh.fairshareapi.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseShareEntry {
    private Long paidBy;
    private double userTotal;
    private double percentShare;
}
