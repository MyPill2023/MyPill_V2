package com.mypill.domain.category.dto.response;

import com.mypill.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoriesResponse {
    private List<CategoryResponse> categories;

    public static CategoriesResponse of(List<Category> categories) {
        return new CategoriesResponse(categories.stream().map(CategoryResponse::of).toList());
    }
}
