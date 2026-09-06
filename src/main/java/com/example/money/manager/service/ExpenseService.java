package com.example.money.manager.service;

import com.example.money.manager.dto.ExpenseDTO;
import com.example.money.manager.entity.CategoryEntity;
import com.example.money.manager.entity.ExpenseEntity;
import com.example.money.manager.entity.ProfileEntity;
import com.example.money.manager.repository.CategoryRepository;
import com.example.money.manager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;


/// adding the expanse into DB

public ExpenseDTO addExpense(ExpenseDTO expenseDTO){
    System.out.println("CATEGORY ID = " + expenseDTO.getCategoryId());
ProfileEntity  profile=profileService.getCurrentProfile();
CategoryEntity category = categoryRepository.findByIdAndProfileId(expenseDTO.getCategoryId(), profile.getId())
        .orElseThrow(()->new RuntimeException("Category Not Found"));
ExpenseEntity newExpense= toEntity(expenseDTO,profile,category);
newExpense=expenseRepository.save(newExpense);
return toDTO(newExpense);

}
/// retrive the expance for current month based on start and end date
public List <ExpenseDTO> getCurrentMonthExpenseForCurrentUser(){
    ProfileEntity profile=profileService.getCurrentProfile();
    LocalDate now=LocalDate.now();
    LocalDate startDate= now.withDayOfMonth(1);
    LocalDate endDate=now.withDayOfMonth(now.getDayOfMonth());
  List<ExpenseEntity> list=  expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
 return list.stream().map(this::toDTO).toList();
}

/// delete expense by id for current user
public void deleteExpense(Long expenseId){
    ProfileEntity profile=profileService.getCurrentProfile();
  ExpenseEntity entity=  expenseRepository.findById(expenseId)
            .orElseThrow(()->new RuntimeException("Expense Not Found"));
  if (!entity.getProfile().getId().equals(profile.getId())){
      throw new RuntimeException("unauthorized to delete this income");
  }
  expenseRepository.delete(entity);
}

///  get latest 5 expenses for the current user
public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
    ProfileEntity profile=profileService.getCurrentProfile();
   List<ExpenseEntity> list= expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
return list.stream().map(this::toDTO).toList();
}

/// get total expenses for current user
public BigDecimal getTotalExpensesForCurrentUser(){
    ProfileEntity profile=profileService.getCurrentProfile();
   BigDecimal total= expenseRepository.findTotalExpenseByProfileId(profile.getId());
  return total != null ? total : BigDecimal.ZERO;
}
///  for the notifications(this is in notification service) we can fetch the expense for the each user for the each day so we can send it to the email
public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
   List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDate(profileId,date);
return list.stream().map(this::toDTO).toList();
}

//filter expenses
public List<ExpenseDTO>  filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
  ProfileEntity profile=  profileService.getCurrentProfile();
    List<ExpenseEntity>list=  expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
   return list.stream().map(this::toDTO).toList();
}

    //helper methods
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
    private ExpenseDTO toDTO(ExpenseEntity entity){
      return  ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
