package ru.practicum.explore.admin.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.dto.CategoryDto;
import ru.practicum.explore.admin.categories.mapper.CategoryMapper;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.InvalidCategoryForDeleteException;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CatRepository repository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public Category addCategory(CategoryDto dto) {
        Category category = mapper.toCategory(dto);
        Category saved = repository.save(category);
        log.debug("Category saved {}", category.getId());
        return saved;
    }

    @Override
    public Category updateCategory(long catId, CategoryDto dto) {
        Category category = checkCategory(catId);
        category.setName(dto.getName());
        return repository.save(category);
    }

    @Override
    public void deleteCategory(long catId) {
        checkCategory(catId);
        List<Event> events = eventRepository.findAllByCategoryId(catId);
        if (events.isEmpty()) {
            repository.deleteById(catId);
        } else {
            throw new InvalidCategoryForDeleteException(String.format("Category contains events and cannot be deleted %d", catId));
        }
    }

    private Category checkCategory(long catId) {
        Category category = repository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException(String.format("Категория не найдена %d", catId)));
        return category;
    }
}
