package com.example.money.manager.controller;

import com.example.money.manager.dto.ExpenseDTO;
import com.example.money.manager.dto.FilterDTO;
import com.example.money.manager.dto.IncomeDTO;
import com.example.money.manager.service.ExpenseService;
import com.example.money.manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor

public class FilterController {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping("filter")
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){

        ///  preparing the data or validation mean we can find the data as per the key word we can soort the data and also find thge data as the name
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.of(2000, 1, 1);
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
String keyword=filter.getKeyword() != null ? filter.getKeyword() : "";
String sortField=filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction="desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);
///  traffic police logic routing
        if("income".equals(filter.getType())){/// // Agar type = "income", toh IncomeService ko call karke Income ka filtered data layega
        List<IncomeDTO> incomes= incomeService.filterIncomes(startDate,endDate,keyword,sort);
         return  ResponseEntity.ok().body(incomes);
        }else if("expense".equals(filter.getType())){  /// // Agar type = "expense", toh ExpenseService ko call karke Expense ka data layega
          List<ExpenseDTO> expenses=  expenseService.filterExpenses(startDate,endDate,keyword,sort);
       return ResponseEntity.ok().body(expenses);
        }else{
            return ResponseEntity.badRequest().body("invalid type must be income or expense");
        }

    }

}
