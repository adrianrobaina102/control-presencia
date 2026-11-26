package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import software.ulpgc.controlpresencia.architecture.model.TrackingInfo;
import software.ulpgc.controlpresencia.architecture.ports.out.EventReader;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HttpEventReader implements EventReader {
    @Override
    public CompletableFuture<List<TrackingInfo>> getEvents(String employeeId, LocalDate date) {
        return CompletableFuture.completedFuture(List.of());
    }
}
