package com.chegg.expensesplitter.service;

import com.chegg.expensesplitter.dto.BalanceResponse;
import com.chegg.expensesplitter.exception.GroupNotFoundException;
import com.chegg.expensesplitter.exception.ValidationException;
import com.chegg.expensesplitter.model.Expense;
import com.chegg.expensesplitter.model.Group;
import com.chegg.expensesplitter.repository.ExpenseRepository;
import com.chegg.expensesplitter.repository.GroupRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private static final int MONEY_SCALE = 2;

    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;

    public BalanceService(GroupRepository groupRepository, ExpenseRepository expenseRepository) {
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> getBalances(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        group.getMembers().forEach(member -> balances.put(member, money(BigDecimal.ZERO)));

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        for (Expense expense : expenses) {
            if (expense.getSplitAmong().isEmpty()) {
                throw new ValidationException("Expense must have split members");
            }

            BigDecimal amount = money(expense.getAmount());
            BigDecimal share = amount.divide(
                    BigDecimal.valueOf(expense.getSplitAmong().size()),
                    MONEY_SCALE,
                    RoundingMode.HALF_UP);

            balances.merge(expense.getPaidBy(), amount, BigDecimal::add);
            expense.getSplitAmong().forEach(member -> balances.merge(member, share.negate(), BigDecimal::add));
        }

        return balances.entrySet()
                .stream()
                .map(entry -> BalanceResponse.builder()
                        .member(entry.getKey())
                        .balance(money(entry.getValue()))
                        .build())
                .toList();
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
