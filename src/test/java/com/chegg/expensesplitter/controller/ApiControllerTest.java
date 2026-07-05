package com.chegg.expensesplitter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chegg.expensesplitter.dto.AddExpenseRequest;
import com.chegg.expensesplitter.dto.BalanceResponse;
import com.chegg.expensesplitter.dto.CreateGroupRequest;
import com.chegg.expensesplitter.dto.ExpenseResponse;
import com.chegg.expensesplitter.dto.GroupResponse;
import com.chegg.expensesplitter.dto.SettlementResponse;
import com.chegg.expensesplitter.dto.SettlementTransaction;
import com.chegg.expensesplitter.exception.GlobalExceptionHandler;
import com.chegg.expensesplitter.exception.GroupNotFoundException;
import com.chegg.expensesplitter.exception.ValidationException;
import com.chegg.expensesplitter.service.BalanceService;
import com.chegg.expensesplitter.service.ExpenseService;
import com.chegg.expensesplitter.service.GroupService;
import com.chegg.expensesplitter.service.SettlementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {GroupController.class, ExpenseController.class, BalanceController.class})
@Import(GlobalExceptionHandler.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupService groupService;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private SettlementService settlementService;

    @Test
    void createGroupReturnsCreatedGroup() throws Exception {
        CreateGroupRequest request = CreateGroupRequest.builder()
                .name("Trip")
                .members(List.of("Alice", "Bob"))
                .build();
        when(groupService.createGroup(any(CreateGroupRequest.class))).thenReturn(GroupResponse.builder()
                .id(1L)
                .name("Trip")
                .members(List.of("Alice", "Bob"))
                .build());

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Trip"))
                .andExpect(jsonPath("$.members[0]").value("Alice"));
    }

    @Test
    void addExpenseReturnsCreatedExpense() throws Exception {
        AddExpenseRequest request = AddExpenseRequest.builder()
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Alice")
                .splitAmong(List.of("Alice", "Bob", "Cara"))
                .build();
        when(expenseService.addExpense(eq(1L), any(AddExpenseRequest.class))).thenReturn(ExpenseResponse.builder()
                .id(10L)
                .groupId(1L)
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Alice")
                .splitAmong(List.of("Alice", "Bob", "Cara"))
                .build());

        mockMvc.perform(post("/api/groups/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.groupId").value(1))
                .andExpect(jsonPath("$.title").value("Dinner"));
    }

    @Test
    void deleteExpenseReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/groups/1/expenses/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getBalancesReturnsBalances() throws Exception {
        when(balanceService.getBalances(1L)).thenReturn(List.of(
                BalanceResponse.builder().member("Alice").balance(new BigDecimal("60.00")).build(),
                BalanceResponse.builder().member("Bob").balance(new BigDecimal("-30.00")).build()));

        mockMvc.perform(get("/api/groups/1/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].member").value("Alice"))
                .andExpect(jsonPath("$[0].balance").value(60.00));
    }

    @Test
    void getSettlementsReturnsTransactions() throws Exception {
        when(settlementService.getSettlements(1L)).thenReturn(SettlementResponse.builder()
                .groupId(1L)
                .transactions(List.of(SettlementTransaction.builder()
                        .from("Bob")
                        .to("Alice")
                        .amount(new BigDecimal("30.00"))
                        .build()))
                .build());

        mockMvc.perform(get("/api/groups/1/settlements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupId").value(1))
                .andExpect(jsonPath("$.transactions[0].from").value("Bob"))
                .andExpect(jsonPath("$.transactions[0].to").value("Alice"))
                .andExpect(jsonPath("$.transactions[0].amount").value(30.00));
    }

    @Test
    void invalidCreateGroupReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "members": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void groupNotFoundReturnsNotFound() throws Exception {
        when(groupService.getGroup(99L)).thenThrow(new GroupNotFoundException("Group not found"));

        mockMvc.perform(get("/api/groups/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Group not found"));
    }

    @Test
    void validationExceptionReturnsUnprocessableEntity() throws Exception {
        AddExpenseRequest request = AddExpenseRequest.builder()
                .title("Dinner")
                .amount(new BigDecimal("90.00"))
                .paidBy("Dana")
                .splitAmong(List.of("Alice", "Bob"))
                .build();
        doThrow(new ValidationException("Paid by member must belong to the group"))
                .when(expenseService)
                .addExpense(eq(1L), any(AddExpenseRequest.class));

        mockMvc.perform(post("/api/groups/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Paid by member must belong to the group"));
    }
}
