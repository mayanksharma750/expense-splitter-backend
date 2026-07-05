package com.chegg.expensesplitter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private Long id;

    private Long groupId;

    private String title;

    private BigDecimal amount;

    private String paidBy;

    @Builder.Default
    private List<String> splitAmong = new ArrayList<>();

    private LocalDateTime createdAt;
}
