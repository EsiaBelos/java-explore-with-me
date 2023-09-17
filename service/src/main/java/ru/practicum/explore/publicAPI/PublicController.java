package ru.practicum.explore.publicAPI;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.publicAPI.service.PublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicController {

    private final PublicService service;

    @GetMapping("/categories")
    public List<Category> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public Category getCatById(@PathVariable Long catId) {
        return service.getCatById(catId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompById(@PathVariable Long compId) {
        return service.getCompById(compId);
    }
}
