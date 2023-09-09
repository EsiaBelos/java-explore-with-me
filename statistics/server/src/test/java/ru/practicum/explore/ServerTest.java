package ru.practicum.explore;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.explore.model.EndpointHit;
import ru.practicum.explore.service.ServerServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.driver-class-name=org.h2.Driver"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerTest {

    @Autowired
    private final ServerServiceImpl service;
    private final EndpointHitDto dto = EndpointHitDto.builder()
            .app("ewm-main-service")
            .ip("192.163.0.1")
            .uri("/events/1")
            .timestamp(
                    LocalDateTime.of(2022, 9, 6,
                            11, 0, 23))
            .build();

    private final EndpointHitDto dto2 = EndpointHitDto.builder()
            .app("ewm-main-service")
            .ip("192.163.0.1")
            .uri("/events/2")
            .timestamp(
                    LocalDateTime.of(2022, 9, 6,
                            11, 0, 23))
            .build();

    @Test
    @Order(1)
    void contextLoads() {
        assertNotNull(service);
    }

    @Test
    @Order(2)
    void testSaveHit() {
        EndpointHit savedHit = service.saveHit(dto);

        assertNotNull(savedHit);
        assertEquals(1L, savedHit.getId());
        assertEquals(dto.getApp(), savedHit.getApp());
        assertEquals(dto.getIp(), savedHit.getIp());
        assertEquals(dto.getUri(), savedHit.getUri());
        assertEquals(dto.getTimestamp(), savedHit.getTimestamp());

        EndpointHit secondHit = service.saveHit(dto);
        assertNotNull(secondHit);
        assertEquals(2L, secondHit.getId());
        service.saveHit(dto2);
    }

    @Test
    @Order(3)
    void testGetHitsWithUriAndUnique() {
        List<ViewStats> results = service.getHits(
                LocalDateTime.of(2022, 9, 5, 11, 0, 23),
                LocalDateTime.of(2022, 9, 7, 11, 0, 23),
                true, List.of("/events/1"));

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto.getApp(), results.get(0).getApp());
        assertEquals(dto.getUri(), results.get(0).getUri());
        assertEquals(1, results.get(0).getHits());
    }

    @Test
    @Order(4)
    void testGetHitsWithUriNotUnique() {
        List<ViewStats> results = service.getHits(
                LocalDateTime.of(2022, 9, 5, 11, 0, 23),
                LocalDateTime.of(2022, 9, 7, 11, 0, 23),
                false, List.of("/events/1"));

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto.getApp(), results.get(0).getApp());
        assertEquals(dto.getUri(), results.get(0).getUri());
        assertEquals(2, results.get(0).getHits());
    }

    @Test
    @Order(5)
    void testGetHitsWithNoUriNotUnique() {
        List<ViewStats> results = service.getHits(
                LocalDateTime.of(2022, 9, 5, 11, 0, 23),
                LocalDateTime.of(2022, 9, 7, 11, 0, 23),
                false, null);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(dto.getApp(), results.get(0).getApp());
        assertEquals(dto.getUri(), results.get(0).getUri());
        assertEquals(2, results.get(0).getHits());
    }

    @Test
    @Order(6)
    void testGetHitsWithNoUriAndUnique() {
        List<ViewStats> results = service.getHits(
                LocalDateTime.of(2022, 9, 5, 11, 0, 23),
                LocalDateTime.of(2022, 9, 7, 11, 0, 23),
                true, null);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(dto.getApp(), results.get(0).getApp());
        assertEquals(dto.getUri(), results.get(0).getUri());
        assertEquals(1, results.get(0).getHits());
    }
}
