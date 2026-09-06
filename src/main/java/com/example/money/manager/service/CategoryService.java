package com.example.money.manager.service;

import com.example.money.manager.dto.CategoryDTO;
import com.example.money.manager.entity.CategoryEntity;
import com.example.money.manager.entity.ProfileEntity;
import com.example.money.manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoryService {
    private final CategoryRepository repository;
    private final ProfileService profileService;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {

        ProfileEntity profile = profileService.getCurrentProfile();  ///Ye current logged-in user nikal raha hai JWT se.

        if(repository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {  // it will check that kya is user ke lie is nam ka category phle se exist krti he if yess throw exception if yes=existsByNameAndProfileId("Food",1)
            throw new RuntimeException( "category with this name already exists");
        }

        CategoryEntity newCategory = toEntity(categoryDTO, profile);

        newCategory = repository.save(newCategory);

        return toDTO(newCategory);
    }

    //get categories for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity>  categories = repository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }
///  get categories by type for current yser
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities=repository.findByTypeAndProfileId(type,profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    ///  to update the category
public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
        ProfileEntity profile= profileService.getCurrentProfile();
     CategoryEntity existingCategory=   repository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(()-> new RuntimeException("sorry the category is not found or not accessible"));
     existingCategory.setName(dto.getName());
     existingCategory.setIcon(dto.getIcon());
     existingCategory=repository.save(existingCategory);
     return toDTO(existingCategory);


}




     // helper methods
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()             /// "Ek nayi CategoryEntity bana."
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();

    }
    private CategoryDTO toDTO(CategoryEntity entity) {
         return CategoryDTO.builder()
                 .id(entity.getId())
                 .profileId(entity.getProfile() !=null? entity.getProfile().getId(): null)
                 .name(entity.getName())
                 .icon(entity.getIcon())
                 .createdAt(entity.getCreatedAt())
                 .updatedAt(entity.getUpdatedAt())
                 .type(entity.getType())


                 .build();
    }
}
