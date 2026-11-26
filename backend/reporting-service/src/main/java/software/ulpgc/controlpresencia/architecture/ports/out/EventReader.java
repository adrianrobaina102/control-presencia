package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.TrackingInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventReader {
    CompletableFuture<List<TrackingInfo>> getEvents(String employeeId, LocalDate date);
}
