package com.chegg.expensesplitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chegg.expensesplitter.dto.BalanceResponse;
import com.chegg.expensesplitter.dto.SettlementResponse;
import com.chegg.expensesplitter.dto.SettlementTransaction;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private SettlementService settlementService;

    @Test
    void getSettlementsUsesGreedyLargestDebtorPaysLargestCreditorAlgorithm() {
        when(balanceService.getBalances(1L)).thenReturn(List.of(
                BalanceResponse.builder().member("Alice").balance(new BigDecimal("50.00")).build(),
                BalanceResponse.builder().member("Bob").balance(new BigDecimal("-30.00")).build(),
                BalanceResponse.builder().member("Cara").balance(new BigDecimal("-20.00")).build()));

        SettlementResponse response = settlementService.getSettlements(1L);

        assertThat(response.getGroupId()).isEqualTo(1L);
        assertThat(response.getTransactions()).hasSize(2);
        assertThat(response.getTransactions())
                .extracting(SettlementTransaction::getFrom)
                .containsExactly("Bob", "Cara");
        assertThat(response.getTransactions())
                .extracting(SettlementTransaction::getTo)
                .containsExactly("Alice", "Alice");
        assertThat(response.getTransactions())
                .extracting(SettlementTransaction::getAmount)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(new BigDecimal("30.00"), new BigDecimal("20.00"));
    }
}
