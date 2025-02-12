package com.flash21.accounting.category.controller;

import com.flash21.accounting.category.dto.request.CategoryRequest;
import com.flash21.accounting.category.dto.response.CategoryResponse;
import com.flash21.accounting.category.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@RequestBody @Valid CategoryRequest categoryRequest,
        @PathVariable(name = "id") Long categoryId) {
        categoryService.updateCategory(categoryRequest, categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
