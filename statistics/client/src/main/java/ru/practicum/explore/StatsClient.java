package ru.practicum.explore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient {
    protected final RestTemplate rest;
    private static final RestTemplateBuilder builder = new RestTemplateBuilder();
    private final String serverUrl = "http://localhost:9090";

    @Autowired
    public StatsClient(@Value("${ewm-server.url}") String url) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveHit(EndpointHitDto dto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(dto, defaultHeaders());
        rest.exchange(serverUrl + "/hit",
                HttpMethod.POST,
                requestEntity,
                Object.class);

    }

    public ResponseEntity<List<ViewStats>> getHits(LocalDateTime start, LocalDateTime end,
                                          Boolean unique, List<String> uris) {
        Map<String, Object> parameters = Map.of(
                "start", start.toString().replace("T", " "),
                "end", end.toString().replace("T", " "),
                "unique", unique,
                "uris", String.join(",", uris)
        );
        String urlTemplate = UriComponentsBuilder.fromHttpUrl("http://localhost:9090/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("unique", "{unique}")
                .queryParam("uris", "{uris}")
                .encode()
                .toUriString();
        return makeAndSendRequestForGet(HttpMethod.GET, urlTemplate, parameters);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private <T> ResponseEntity<List<ViewStats>> makeAndSendRequestForGet(HttpMethod method, String path, Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<List<ViewStats>> serverResponse;
                log.info("Parameters: {}", parameters);
                serverResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<List<ViewStats>>() {}, parameters);

        return prepareGatewayResponseForGet(serverResponse);
    }

    private static ResponseEntity<List<ViewStats>> prepareGatewayResponseForGet(ResponseEntity<List<ViewStats>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}