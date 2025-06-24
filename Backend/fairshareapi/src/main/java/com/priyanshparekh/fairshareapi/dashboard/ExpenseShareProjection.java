package com.priyanshparekh.fairshareapi.dashboard;

public interface ExpenseShareProjection {

    Long getPaidBy();
    Double getUserTotal();
    Double getPercentShare();

}
