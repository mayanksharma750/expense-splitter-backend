package com.chegg.expensesplitter.repository;

import com.chegg.expensesplitter.model.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByGroupId(Long groupId);

    Optional<Expense> findById(Long id);

    void deleteById(Long id);
}
