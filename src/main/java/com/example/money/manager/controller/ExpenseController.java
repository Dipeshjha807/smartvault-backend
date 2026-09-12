    package com.example.money.manager.controller;
    
    import com.example.money.manager.dto.ExpenseDTO;
    import com.example.money.manager.service.ExpenseService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    
    import java.util.List;
    
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("expenses")
    public class ExpenseController {
        private final ExpenseService expenseService;
    
        @PostMapping        ///  for saving the expense
        public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto){
            System.out.println("EXPENSE CONTROLLER HIT");
    
    ExpenseDTO  saved=expenseService.addExpense(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }
        @GetMapping  ///  getting the expense for the current expenses not the entire expenses for the DB
        public ResponseEntity<List<ExpenseDTO>> getExpenses(){
           List<ExpenseDTO> expenses= expenseService.getCurrentMonthExpenseForCurrentUser();
           return ResponseEntity.ok(expenses);
        }
    
        @DeleteMapping("{id}")
        public ResponseEntity <Void> deleteExpense(@PathVariable Long id){
            expenseService.deleteExpense(id);
            return ResponseEntity.noContent().build();
        }
    
    }
