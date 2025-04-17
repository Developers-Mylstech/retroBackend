package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.CategoryRequest;
import com.mylstech.rentro.dto.response.CategoryResponse;
import com.mylstech.rentro.model.Category;
import com.mylstech.rentro.repository.CategoryRepository;
import com.mylstech.rentro.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return new CategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = request.requestToCategory();
        
        // Set parent category if parentCategoryId is provided
        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }
        
        return new CategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        
        // Update parent category if parentCategoryId is provided
        if (request.getParentCategoryId() != null) {
            // Prevent setting a category as its own parent
            if (request.getParentCategoryId().equals(id)) {
                throw new RuntimeException("A category cannot be its own parent");
            }
            
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentCategoryId()));
            
            // Check for circular reference
            Category current = parentCategory;
            while (current != null) {
                if (current.getCategoryId().equals(id)) {
                    throw new RuntimeException("Circular reference detected: a category cannot have itself as an ancestor");
                }
                current = current.getParentCategory();
            }
            
            category.setParentCategory(parentCategory);
        } else if (request.getParentCategoryId() == null && category.getParentCategory() != null) {
            // If parentCategoryId is explicitly set to null, remove the parent
            category.setParentCategory(null);
        }
        
        return new CategoryResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // If the category has subcategories, either delete them or reassign them
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            // Option 1: Delete all subcategories (cascading delete)
            // This is handled by CascadeType.ALL in the entity
            
            // Option 2: Reassign subcategories to the parent of the category being deleted
            // Uncomment the following code to implement this option
            /*
            Category parentCategory = category.getParentCategory();
            for (Category subCategory : category.getSubCategories()) {
                subCategory.setParentCategory(parentCategory);
                categoryRepository.save(subCategory);
            }
            */
        }
        
        categoryRepository.delete(category);
    }
}
