package com.priyanshparekh.fairshareapi.dashboard;

public interface DailyExpenseProjection {

    String getExpenseDate(); // or use java.sql.Date
    Double getTotalAmount();

}
