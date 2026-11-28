package tests;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.ScheduleInfo;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.HttpScheduleReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpScheduleReaderTest {

    private HttpServer server;
    private HttpScheduleReader reader;

    @BeforeEach
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/schedules", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if ("/schedules/SCH-01".equals(path)) {
                String json = """
                        {
                          "id": "SCH-01",
                          "name": "Oficina",
                          "courtesyMinutes": 10,
                          "exitMarginMinutes": 5,
                          "intervals": [{"start": "08:00", "end": "16:00"}],
                          "days": ["MONDAY", "TUESDAY"]
                        }
                        """;
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
            exchange.close();
        });
        server.start();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, _, _) -> LocalTime.parse(json.getAsString()))
                .create();
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        reader = new HttpScheduleReader(HttpClient.newHttpClient(), baseUrl, gson);
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void should_return_schedule_info_when_status_is_200() {
        Optional<ScheduleInfo> result = reader.get("SCH-01").join();

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("SCH-01");
        assertThat(result.get().courtesyMinutes()).isEqualTo(10);
        assertThat(result.get().totalExpectedMinutes()).isEqualTo(480);
        assertThat(result.get().appliesOn(DayOfWeek.MONDAY)).isTrue();
    }

    @Test
    public void should_return_empty_when_status_is_404() {
        Optional<ScheduleInfo> result = reader.get("unknown").join();

        assertThat(result).isEmpty();
    }
}
