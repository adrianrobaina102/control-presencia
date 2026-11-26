package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import software.ulpgc.controlpresencia.architecture.model.TrackingInfo;
import software.ulpgc.controlpresencia.architecture.model.TrackingType;
import software.ulpgc.controlpresencia.architecture.ports.out.EventReader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HttpEventReader implements EventReader {
    private final HttpClient client;
    private final String baseUrl;

    public HttpEventReader(HttpClient client, String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    @Override
    public CompletableFuture<List<TrackingInfo>> getEvents(String employeeId, LocalDate date) {
        return findEvents(employeeId, date);
    }

    private CompletableFuture<List<TrackingInfo>> findEvents(String employeeId, LocalDate date) {
        return client.sendAsync(requestWith(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseEvents(response, employeeId, date))
                .exceptionally(_ -> List.of());
    }

    private List<TrackingInfo> parseEvents(HttpResponse<String> response, String employeeId, LocalDate date) {
        if (response.statusCode() != 200 || response.body() == null) return List.of();
        return response.body().lines()
                .filter(line -> !line.isBlank())
                .map(this::separateFields)
                .filter(fields -> fields.length >= 4)
                .filter(fields -> fields[1].equals(employeeId))
                .filter(fields -> LocalDate.ofInstant(Instant.parse(fields[0]), ZoneId.systemDefault()).equals(date))
                .map(this::toTrackingInfo)
                .toList();
    }

    private TrackingInfo toTrackingInfo(String[] fields) {
        return new TrackingInfo(
                Instant.parse(fields[0]),
                fields[1],
                fields[2],
                TrackingType.valueOf(fields[3])
        );
    }

    private String[] separateFields(String line) {
        return line.split(";");
    }

    private HttpRequest requestWith() {
        return HttpRequest.newBuilder(URI.create(baseUrl + "/"))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
    }
}
