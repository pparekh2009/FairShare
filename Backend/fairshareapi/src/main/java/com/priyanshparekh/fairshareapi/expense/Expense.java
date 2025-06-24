package com.priyanshparekh.fairshareapi.expense;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.regex.Pattern;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long groupId;
    private String name;
    private Double amount;
    private String note;
    private Long paidBy;
    private Long createdAt;
    private String receiptUrl;

}
