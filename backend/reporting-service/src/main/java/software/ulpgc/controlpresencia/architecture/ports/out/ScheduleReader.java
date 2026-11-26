package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.ScheduleInfo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ScheduleReader {
    CompletableFuture<Optional<ScheduleInfo>> get(String id);
}
