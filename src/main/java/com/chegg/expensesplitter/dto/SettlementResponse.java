package com.chegg.expensesplitter.dto;

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
public class SettlementResponse {

    private Long groupId;

    @Builder.Default
    private List<SettlementTransaction> transactions = new ArrayList<>();
}
