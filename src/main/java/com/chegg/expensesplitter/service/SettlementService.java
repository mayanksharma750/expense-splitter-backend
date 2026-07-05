package com.chegg.expensesplitter.service;

import com.chegg.expensesplitter.dto.BalanceResponse;
import com.chegg.expensesplitter.dto.SettlementResponse;
import com.chegg.expensesplitter.dto.SettlementTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {

    private static final int MONEY_SCALE = 2;

    private final BalanceService balanceService;

    public SettlementService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public SettlementResponse getSettlements(Long groupId) {
        return SettlementResponse.builder()
                .groupId(groupId)
                .transactions(calculateTransactions(balanceService.getBalances(groupId)))
                .build();
    }

    public List<SettlementTransaction> calculateTransactions(List<BalanceResponse> balances) {
        List<MemberBalance> creditors = balances.stream()
                .filter(balance -> balance.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .map(balance -> new MemberBalance(balance.getMember(), money(balance.getBalance())))
                .sorted(Comparator.comparing(MemberBalance::amount).reversed())
                .toList();

        List<MemberBalance> debtors = balances.stream()
                .filter(balance -> balance.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .map(balance -> new MemberBalance(balance.getMember(), money(balance.getBalance().abs())))
                .sorted(Comparator.comparing(MemberBalance::amount).reversed())
                .toList();

        List<MemberBalance> remainingCreditors = new java.util.ArrayList<>(creditors);
        List<MemberBalance> remainingDebtors = new java.util.ArrayList<>(debtors);
        List<SettlementTransaction> transactions = new java.util.ArrayList<>();

        int debtorIndex = 0;
        int creditorIndex = 0;

        while (debtorIndex < remainingDebtors.size() && creditorIndex < remainingCreditors.size()) {
            MemberBalance debtor = remainingDebtors.get(debtorIndex);
            MemberBalance creditor = remainingCreditors.get(creditorIndex);
            BigDecimal payment = debtor.amount().min(creditor.amount());

            if (payment.compareTo(BigDecimal.ZERO) > 0) {
                transactions.add(SettlementTransaction.builder()
                        .from(debtor.member())
                        .to(creditor.member())
                        .amount(money(payment))
                        .build());
            }

            remainingDebtors.set(debtorIndex, debtor.subtract(payment));
            remainingCreditors.set(creditorIndex, creditor.subtract(payment));

            if (remainingDebtors.get(debtorIndex).amount().compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }

            if (remainingCreditors.get(creditorIndex).amount().compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }
        }

        return transactions;
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private record MemberBalance(String member, BigDecimal amount) {

        private MemberBalance subtract(BigDecimal value) {
            return new MemberBalance(member, amount.subtract(value).setScale(MONEY_SCALE, RoundingMode.HALF_UP));
        }
    }
}
