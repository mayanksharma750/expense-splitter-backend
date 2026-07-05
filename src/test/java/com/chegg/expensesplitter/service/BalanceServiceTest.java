package com.chegg.expensesplitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chegg.expensesplitter.dto.BalanceResponse;
import com.chegg.expensesplitter.model.Expense;
import com.chegg.expensesplitter.model.Group;
import com.chegg.expensesplitter.repository.ExpenseRepository;
import com.chegg.expensesplitter.repository.GroupRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void getBalancesComputesBalancesDynamically() {
        Group group = Group.builder()
                .id(1L)
                .name("Trip")
                .members(List.of("Alice", "Bob", "Cara"))
                .build();
        Expense expense = Expense.builder()
                .id(10L)
                .group(group)
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Alice")
                .splitAmong(List.of("Alice", "Bob", "Cara"))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(expenseRepository.findByGroupId(1L)).thenReturn(List.of(expense));

        Map<String, BigDecimal> balances = balanceService.getBalances(1L)
                .stream()
                .collect(Collectors.toMap(BalanceResponse::getMember, BalanceResponse::getBalance));

        assertThat(balances.get("Alice")).isEqualByComparingTo("60.00");
        assertThat(balances.get("Bob")).isEqualByComparingTo("-30.00");
        assertThat(balances.get("Cara")).isEqualByComparingTo("-30.00");
    }
}
