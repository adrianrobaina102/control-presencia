package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.EmployeeInfo;
import software.ulpgc.controlpresencia.architecture.ports.out.EmployeeReader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HttpEmployeeReader implements EmployeeReader {
    private record ContractPayload(String scheduleId) {}
    private record EmployeePayload(String id, String name, ContractPayload contract) {}

    private final HttpClient client;
    private final String baseUrl;
    private final Gson gson;

    public HttpEmployeeReader(HttpClient client, String baseUrl, Gson gson) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.gson = gson;
    }

    @Override
    public CompletableFuture<Optional<EmployeeInfo>> get(String id) {
        return findEmployee(id);
    }

    private CompletableFuture<Optional<EmployeeInfo>> findEmployee(String id) {
        return client.sendAsync(requestWith(id), HttpResponse.BodyHandlers.ofString())
                .thenApply(this::parseEmployee)
                .exceptionally(_ -> Optional.empty());
    }

    private Optional<EmployeeInfo> parseEmployee(HttpResponse<String> response) {
        if (response.statusCode() != 200) return Optional.empty();
        EmployeePayload payload = gson.fromJson(response.body(), EmployeePayload.class);
        if (payload == null || payload.contract() == null) return Optional.empty();
        return Optional.of(new EmployeeInfo(payload.id(), payload.name(), payload.contract().scheduleId()));
    }

    private HttpRequest requestWith(String id) {
        return HttpRequest.newBuilder(URI.create(baseUrl +  "/employees?id="  + id))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
    }
}
