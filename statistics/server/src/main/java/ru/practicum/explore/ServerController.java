package ru.practicum.explore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.service.ServerService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ServerController {

    private final ServerService service;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Valid EndpointHitDto dto) {
        service.saveHit(dto);
    }

    @GetMapping(value = "/stats")
    public List<ViewStats> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(defaultValue = "false") Boolean unique,
                                   @RequestParam(required = false) List<String> uris) {
        List<ViewStats> views = service.getHits(start, end, unique, uris);
        log.info("List received by controller {}", views != null);
        return views;
    }
}
