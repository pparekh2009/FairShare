package com.priyanshparekh.fairshareapi.balanceinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayData {

    private Long payerId;
    private Long payeeId;
    private Double amount;
    private String note;
}
