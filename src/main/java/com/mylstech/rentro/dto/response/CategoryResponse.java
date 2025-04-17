package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long categoryId;
    private String name;
    private Long parentCategoryId;
    private List<CategoryResponse> subCategories;

    public CategoryResponse(Category category) {
        this.categoryId = category.getCategoryId();
        this.name = category.getName();
        
        if (category.getParentCategory() != null) {
            this.parentCategoryId = category.getParentCategory().getCategoryId();
        }
        
        if (category.getSubCategories() != null) {
            this.subCategories = category.getSubCategories().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
        }
    }
}
