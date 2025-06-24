package com.priyanshparekh.fairshareapi.balanceinfo;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance_info")
public class BalanceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long groupId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private Direction direction;
    private Long otherUserId;
    private Double amount;

    protected enum Direction {
        OWES_TO,
        RECEIVES_FROM
    }

}
