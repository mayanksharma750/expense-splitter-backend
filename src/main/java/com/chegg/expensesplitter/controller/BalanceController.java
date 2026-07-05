package com.chegg.expensesplitter.controller;

import com.chegg.expensesplitter.dto.BalanceResponse;
import com.chegg.expensesplitter.dto.SettlementResponse;
import com.chegg.expensesplitter.service.BalanceService;
import com.chegg.expensesplitter.service.SettlementService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups/{id}")
public class BalanceController {

    private final BalanceService balanceService;
    private final SettlementService settlementService;

    public BalanceController(BalanceService balanceService, SettlementService settlementService) {
        this.balanceService = balanceService;
        this.settlementService = settlementService;
    }

    @GetMapping("/balances")
    public ResponseEntity<List<BalanceResponse>> getBalances(@PathVariable Long id) {
        return ResponseEntity.ok(balanceService.getBalances(id));
    }

    @GetMapping("/settlements")
    public ResponseEntity<SettlementResponse> getSettlements(@PathVariable Long id) {
        return ResponseEntity.ok(settlementService.getSettlements(id));
    }
}
