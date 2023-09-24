package ru.practicum.explore.admin.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class CompilationDto {
    private Long id;
    private List<ShortEventDto> events;
    private boolean pinned;
    private String title;

    public CompilationDto(Long id, boolean pinned, String title) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
        this.events = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilationDto that = (CompilationDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
