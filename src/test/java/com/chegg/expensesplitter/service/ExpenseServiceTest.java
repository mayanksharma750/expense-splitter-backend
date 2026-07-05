package com.chegg.expensesplitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chegg.expensesplitter.dto.AddExpenseRequest;
import com.chegg.expensesplitter.dto.ExpenseResponse;
import com.chegg.expensesplitter.exception.ExpenseNotFoundException;
import com.chegg.expensesplitter.exception.ValidationException;
import com.chegg.expensesplitter.model.Expense;
import com.chegg.expensesplitter.model.Group;
import com.chegg.expensesplitter.repository.ExpenseRepository;
import com.chegg.expensesplitter.repository.GroupRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void addExpenseSavesExpenseAndReturnsDto() {
        Group group = group();
        AddExpenseRequest request = AddExpenseRequest.builder()
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Alice")
                .splitAmong(List.of("Alice", "Bob", "Cara"))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            expense.setId(10L);
            return expense;
        });

        ExpenseResponse response = expenseService.addExpense(1L, request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getGroupId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Dinner");
        assertThat(response.getAmount()).isEqualByComparingTo("90.00");
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void addExpenseThrowsValidationExceptionWhenPayerIsNotGroupMember() {
        AddExpenseRequest request = AddExpenseRequest.builder()
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Dana")
                .splitAmong(List.of("Alice", "Bob"))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group()));

        assertThatThrownBy(() -> expenseService.addExpense(1L, request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Paid by member must belong to the group");
    }

    @Test
    void deleteExpenseDeletesExpenseWhenItBelongsToGroup() {
        Group group = group();
        Expense expense = Expense.builder()
                .id(10L)
                .group(group)
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Alice")
                .splitAmong(List.of("Alice", "Bob", "Cara"))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

        expenseService.deleteExpense(1L, 10L);

        verify(expenseRepository).deleteById(10L);
    }

    @Test
    void deleteExpenseThrowsWhenExpenseDoesNotExist() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group()));
        when(expenseRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.deleteExpense(1L, 10L))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found");
    }

    private Group group() {
        return Group.builder()
                .id(1L)
                .name("Trip")
                .members(List.of("Alice", "Bob", "Cara"))
                .build();
    }
}
