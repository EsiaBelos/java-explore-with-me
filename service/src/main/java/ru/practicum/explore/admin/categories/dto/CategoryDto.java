package ru.practicum.explore.admin.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Validated
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    @NotBlank
    @Size(max = 50)
    private String name;

}
