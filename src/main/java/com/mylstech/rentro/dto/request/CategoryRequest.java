package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    private String name;
    private Long parentCategoryId; // ID of the parent category (if this is a subcategory)
    private List<String> images;
    public Category requestToCategory() {
        Category category = new Category();
        category.setName(name);
        category.setImageUrls ( images );
        // Note: parentCategory will be set in the service layer
        return category;
    }
}
