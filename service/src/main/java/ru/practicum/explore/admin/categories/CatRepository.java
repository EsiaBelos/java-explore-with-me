package ru.practicum.explore.admin.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.admin.categories.model.Category;

public interface CatRepository extends JpaRepository<Category, Long> {
}
