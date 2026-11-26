package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.ScheduleInfo;
import software.ulpgc.controlpresencia.architecture.ports.out.ScheduleReader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HttpScheduleReader implements ScheduleReader {
    private final HttpClient client;
    private final Gson gson;
    private final String baseUrl;

    public HttpScheduleReader(HttpClient client, String baseUrl, Gson gson) {
        this.client = client;
        this.gson = gson;
        this.baseUrl = baseUrl;
    }

    @Override
    public CompletableFuture<Optional<ScheduleInfo>> get(String id) {
        return findSchedule(id);
    }

    private CompletableFuture<Optional<ScheduleInfo>> findSchedule(String id) {
        return client.sendAsync(requestWith(id), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200
                        ? Optional.ofNullable(gson.fromJson(response.body(), ScheduleInfo.class))
                        : Optional.<ScheduleInfo>empty())
                .exceptionally(_ -> Optional.empty());
    }

    private HttpRequest requestWith(String id) {
        return HttpRequest.newBuilder(URI.create(baseUrl + "/schedules?id=" + id))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
    }
}
