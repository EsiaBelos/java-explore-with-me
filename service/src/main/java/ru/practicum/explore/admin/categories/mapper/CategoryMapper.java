package ru.practicum.explore.admin.categories.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.categories.dto.CategoryDto;
import ru.practicum.explore.admin.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryDto dto);
}
