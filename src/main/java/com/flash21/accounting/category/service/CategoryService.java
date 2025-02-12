package com.flash21.accounting.category.service;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.dto.request.CategoryRequest;
import com.flash21.accounting.category.dto.response.CategoryResponse;
import com.flash21.accounting.category.repository.CategoryRepository;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.CategoryErrorCode;
import com.flash21.accounting.common.util.EntityToDtoMapper;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = categoryRepository.save(convertToEntity(categoryRequest));
        return convertToDto(category);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional
    public void updateCategory(CategoryRequest categoryRequest, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> AccountingException.of(CategoryErrorCode.NOT_FOUND_CATEGORY));
        category.updateCategoryName(categoryRequest.name());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> AccountingException.of(CategoryErrorCode.NOT_FOUND_CATEGORY));
        categoryRepository.delete(category);
    }


    public Category convertToEntity(CategoryRequest categoryRequest) {
        return EntityToDtoMapper.INSTANCE.dtoToCategory(categoryRequest);
    }

    public CategoryResponse convertToDto(Category category) {
        return EntityToDtoMapper.INSTANCE.categoryToDto(category);
    }
}
