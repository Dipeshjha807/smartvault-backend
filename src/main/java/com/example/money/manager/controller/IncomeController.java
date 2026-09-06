package com.example.money.manager.controller;

import com.example.money.manager.dto.ExpenseDTO;
import com.example.money.manager.dto.IncomeDTO;
import com.example.money.manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("incomes")
public class IncomeController {
    private final IncomeService incomeService;
    @PostMapping
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO dto) {
        System.out.println("========== INCOME CONTROLLER HIT ==========");
        System.out.println("Category ID = " + dto.getCategoryId());
        IncomeDTO saved = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
        @GetMapping
        public ResponseEntity<List<IncomeDTO>> getIncomes(){
            List<IncomeDTO> expenses= incomeService.getCurrentMonthIncomesForCurrentUser();
            return ResponseEntity.ok(expenses);
        }


    @DeleteMapping("{id}")
    public ResponseEntity <Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }

    }
