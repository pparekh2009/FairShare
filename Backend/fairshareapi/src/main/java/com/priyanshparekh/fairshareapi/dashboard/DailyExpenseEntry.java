package com.priyanshparekh.fairshareapi.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DailyExpenseEntry {
    private String expenseDate;
    private double totalAmount;
}
