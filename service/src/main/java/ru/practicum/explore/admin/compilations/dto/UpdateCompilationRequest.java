package ru.practicum.explore.admin.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Validated
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @Size(max = 50, min = 1)
    private String title;
}

