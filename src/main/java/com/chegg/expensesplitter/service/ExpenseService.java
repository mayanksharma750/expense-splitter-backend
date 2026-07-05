package com.chegg.expensesplitter.service;

import com.chegg.expensesplitter.dto.AddExpenseRequest;
import com.chegg.expensesplitter.dto.ExpenseResponse;
import com.chegg.expensesplitter.exception.ExpenseNotFoundException;
import com.chegg.expensesplitter.exception.GroupNotFoundException;
import com.chegg.expensesplitter.exception.ValidationException;
import com.chegg.expensesplitter.model.Expense;
import com.chegg.expensesplitter.model.Group;
import com.chegg.expensesplitter.repository.ExpenseRepository;
import com.chegg.expensesplitter.repository.GroupRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository, GroupRepository groupRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
    }

    public ExpenseResponse addExpense(Long groupId, AddExpenseRequest request) {
        Group group = getGroupEntity(groupId);
        validateMembers(group, request);

        Expense expense = Expense.builder()
                .group(group)
                .title(request.getTitle())
                .amount(request.getAmount())
                .paidBy(request.getPaidBy())
                .splitAmong(new ArrayList<>(request.getSplitAmong()))
                .build();

        return mapToResponse(expenseRepository.save(expense));
    }

    public List<ExpenseResponse> getExpenses(Long groupId) {
        getGroupEntity(groupId);

        return expenseRepository.findByGroupId(groupId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteExpense(Long groupId, Long expenseId) {
        getGroupEntity(groupId);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        if (!expense.getGroup().getId().equals(groupId)) {
            throw new ExpenseNotFoundException("Expense not found");
        }

        expenseRepository.deleteById(expenseId);
    }

    private Group getGroupEntity(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));
    }

    private void validateMembers(Group group, AddExpenseRequest request) {
        Set<String> groupMembers = new HashSet<>(group.getMembers());

        if (!groupMembers.contains(request.getPaidBy())) {
            throw new ValidationException("Paid by member must belong to the group");
        }

        if (!groupMembers.containsAll(request.getSplitAmong())) {
            throw new ValidationException("Split members must belong to the group");
        }
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .groupId(expense.getGroup().getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .paidBy(expense.getPaidBy())
                .splitAmong(new ArrayList<>(expense.getSplitAmong()))
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
