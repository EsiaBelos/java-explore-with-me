import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explore.StatsClient;

@SpringBootTest
public class StatsClientTest {
    StatsClient client = new StatsClient("http://localhost:9090");


}
