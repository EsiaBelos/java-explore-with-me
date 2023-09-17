package ru.practicum.explore.admin.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Getter
@Validated
@Setter
@AllArgsConstructor
public class UserDto {
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    private String email;
}
