package ru.practicum.explore.admin.categories.service;

import ru.practicum.explore.admin.categories.dto.CategoryDto;
import ru.practicum.explore.admin.categories.model.Category;

public interface CategoryService {
    Category addCategory(CategoryDto dto);

    Category updateCategory(long catId, CategoryDto dto);

    void deleteCategory(long catId);
}
