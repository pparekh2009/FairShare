package com.priyanshparekh.fairshareapi.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetBalanceEntry {

    private Long userId;
    private Double netBalance;

}
