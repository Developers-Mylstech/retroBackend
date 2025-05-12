package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.CategoryRequest;
import com.mylstech.rentro.dto.response.CategoryResponse;
import com.mylstech.rentro.model.Category;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.CategoryRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if the category has subcategories
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete category with subcategories. Found " + 
                    category.getSubCategories().size() + " subcategories. Remove subcategories first.");
        }

        // Check if any products are associated with this category
        List<Product> productsWithCategory = productRepository.findByCategoryCategoryId(id);
        if (productsWithCategory != null && !productsWithCategory.isEmpty()) {
            throw new RuntimeException("Cannot delete category with associated products. Found " + 
                    productsWithCategory.size() + " products using this category.");
        }

        // Check if any products are associated with this category as a subcategory
        List<Product> productsWithSubCategory = productRepository.findBySubCategoryCategoryId(id);
        if (productsWithSubCategory != null && !productsWithSubCategory.isEmpty()) {
            throw new RuntimeException("Cannot delete category with associated products as subcategory. Found " + 
                    productsWithSubCategory.size() + " products using this as subcategory.");
        }

        // Clear images if any
        if (category.getImages() != null) {
            category.getImages().clear();
        }

        // Clear image URLs if any
        if (category.getImageUrls() != null) {
            category.getImageUrls().clear();
        }

        // Save the category with cleared collections first
        categoryRepository.save(category);

        // Now delete the category
        categoryRepository.delete(category);
    }
}
