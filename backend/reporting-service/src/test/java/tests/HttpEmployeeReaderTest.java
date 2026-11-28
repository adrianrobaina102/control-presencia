package tests;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.EmployeeInfo;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.HttpEmployeeReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpEmployeeReaderTest {

    private HttpServer server;
    private HttpEmployeeReader reader;

    @BeforeEach
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/employees", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if ("/employees/emp-01".equals(path)) {
                String json = """
                        {"id":"emp-01","name":"Adrián Robaina","contract":{"scheduleId":"SCH-OFFICE-CONTINUOUS"}}
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
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        reader = new HttpEmployeeReader(HttpClient.newHttpClient(), baseUrl, new Gson());
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void should_return_employee_info_when_status_is_200() {
        Optional<EmployeeInfo> result = reader.get("emp-01").join();

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("emp-01");
        assertThat(result.get().name()).isEqualTo("Adrián Robaina");
        assertThat(result.get().scheduleId()).isEqualTo("SCH-OFFICE-CONTINUOUS");
    }

    @Test
    public void should_return_empty_when_status_is_404() {
        Optional<EmployeeInfo> result = reader.get("unknown").join();

        assertThat(result).isEmpty();
    }
}
