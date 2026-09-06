package com.example.money.manager.service;

 import com.example.money.manager.dto.ExpenseDTO;
 import com.example.money.manager.dto.IncomeDTO;
import com.example.money.manager.entity.CategoryEntity;
 import com.example.money.manager.entity.ExpenseEntity;
 import com.example.money.manager.entity.IncomeEntity;
import com.example.money.manager.entity.ProfileEntity;
 import com.example.money.manager.repository.CategoryRepository;
 import com.example.money.manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
 import org.springframework.data.domain.Sort;
 import org.springframework.stereotype.Service;

 import java.math.BigDecimal;
 import java.time.LocalDate;
 import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    /// adding the expanse into DB

    public IncomeDTO addIncome(IncomeDTO incomeDTO){
        ProfileEntity  profile=profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category Not Found"));
        IncomeEntity newExpense= toEntity(incomeDTO,profile,category);
        newExpense=incomeRepository.save(newExpense);
    return toDTO(newExpense);

    }

    // retrive the expance for current month based on start and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate= now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.getDayOfMonth());
        List<IncomeEntity> list=  incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }



    /// delete expense by id for current user
    public void deleteIncome(Long incomeId){
        ProfileEntity profile=profileService.getCurrentProfile();
        IncomeEntity entity=  incomeRepository.findById(incomeId)
                .orElseThrow(()->new RuntimeException("income Not Found"));
        if (!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("unauthorized to delete this income ");
        }
        incomeRepository.delete(entity);
    }

    ///  get latest 5 inocme for the current user
    public List<IncomeDTO> getLatest5IncomeForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list= incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    /// get total income for current user
    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total= incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }


    //filter expenses
    public List<IncomeDTO>  filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=  profileService.getCurrentProfile();
        List<IncomeEntity>list=  incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }

    //helper methods
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
    private IncomeDTO toDTO(IncomeEntity entity){
        return  IncomeDTO.builder()
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
