package ru.practicum.explore;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.service.ServerService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final ServerService service;

    @PostMapping(value = "/hit")
    public ResponseEntity<String> saveHit(@RequestBody @Valid EndpointHitDto dto) {
        service.saveHit(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Информация сохранена");
    }

    @GetMapping(value = "/stats")
    public List<ViewStats> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(defaultValue = "false") Boolean unique,
                                   @RequestParam(required = false) List<String> uris) {
        return service.getHits(start, end, unique, uris);
    }
}
