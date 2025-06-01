package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    private String name;
    private Long parentCategoryId; // ID of the parent category (if this is a subcategory)

    public Category requestToCategory() {
        Category category = new Category ( );
        category.setName ( name );


        // Note: parentCategory will be set in the service layer
        return category;
    }
}
