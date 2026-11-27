package tests;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.TrackingInfo;
import software.ulpgc.controlpresencia.architecture.model.TrackingType;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.HttpEventReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpEventReaderTest {

    private static final LocalDate TARGET_DATE = LocalDate.of(2026, 11, 2);
    private static final LocalDate OTHER_DATE = LocalDate.of(2026, 11, 3);

    private HttpServer server;
    private HttpEventReader reader;

    @BeforeEach
    public void setUp() throws IOException {
        String tsIn = LocalDateTime.of(TARGET_DATE, LocalTime.of(8, 0)).atZone(ZoneId.systemDefault()).toInstant().toString();
        String tsOut = LocalDateTime.of(TARGET_DATE, LocalTime.of(16, 0)).atZone(ZoneId.systemDefault()).toInstant().toString();
        String tsOtherDay = LocalDateTime.of(OTHER_DATE, LocalTime.of(9, 0)).atZone(ZoneId.systemDefault()).toInstant().toString();

        String body = String.join("\n",
                tsIn + ";emp-01;Oficina;In",
                tsOut + ";emp-01;Oficina;Out",
                tsIn + ";emp-02;Sede Sur;In",
                tsOtherDay + ";emp-01;Oficina;In"
        );

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        });
        server.start();
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        reader = new HttpEventReader(HttpClient.newHttpClient(), baseUrl);
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void should_filter_events_by_employee_and_date() {
        List<TrackingInfo> events = reader.getEvents("emp-01", TARGET_DATE).join();

        assertThat(events).hasSize(2);
        assertThat(events.get(0).type()).isEqualTo(TrackingType.In);
        assertThat(events.get(1).type()).isEqualTo(TrackingType.Out);
    }

    @Test
    public void should_return_empty_list_when_no_events_match_employee() {
        List<TrackingInfo> events = reader.getEvents("emp-99", TARGET_DATE).join();

        assertThat(events).isEmpty();
    }
}
