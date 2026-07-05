package com.chegg.expensesplitter.controller;

import com.chegg.expensesplitter.dto.AddExpenseRequest;
import com.chegg.expensesplitter.dto.ExpenseResponse;
import com.chegg.expensesplitter.service.ExpenseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups/{id}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable Long id,
            @Valid @RequestBody AddExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.addExpense(id, request));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenses(id));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @PathVariable Long expenseId) {
        expenseService.deleteExpense(id, expenseId);
        return ResponseEntity.noContent().build();
    }
}
