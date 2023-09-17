package ru.practicum.explore.admin.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.categories.dto.CategoryDto;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.categories.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody @Valid CategoryDto dto) {
        return service.addCategory(dto);
    }

    @PatchMapping("/{catId}")
    public Category updateCategory(@PathVariable long catId, @RequestBody @Valid CategoryDto dto) {
        return service.updateCategory(catId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        service.deleteCategory(catId);
    }
}
