import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.StatsClient;

import java.time.LocalDateTime;

@SpringBootTest
@RequiredArgsConstructor
public class StatsClientTest {
    private final StatsClient client;

    @Test
    void test() {
        String artifact = "explore-with-me-service";
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .ip("192.168.123.132")
                .uri("http://localhost:8080/users/1/events/1")
                .timestamp(LocalDateTime.now())
                .app(artifact)
                .build();
        ResponseEntity<Object> response = client.saveHit(hitDto);


    }

}
