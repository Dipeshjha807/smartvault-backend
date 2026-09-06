package com.example.money.manager.controller;

import com.example.money.manager.dto.CategoryDTO;
import com.example.money.manager.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping  // saving category
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {

        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
    @GetMapping            ///  this method is for getting all categores for the current user who is loggrd in the application
    public ResponseEntity<List<CategoryDTO>> getCategories() {
List<CategoryDTO> categories= categoryService.getCategoriesForCurrentUser();
return ResponseEntity.ok(categories);

    }
    ///  we want to pass the category type the imcome or expance type for the current user thats the purpose for the controller means we can get the category by the type
    @GetMapping("{type}")
    public ResponseEntity<List<CategoryDTO> >getCategoriesByTypeForCurrentUser(@PathVariable String type) {
        List<CategoryDTO> list= categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(list);
    }
    @PutMapping("{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId , @RequestBody CategoryDTO categoryDTO) {
     CategoryDTO updateCategory=   categoryService.updateCategory(categoryId,categoryDTO);
     return ResponseEntity.ok(updateCategory);
}}
