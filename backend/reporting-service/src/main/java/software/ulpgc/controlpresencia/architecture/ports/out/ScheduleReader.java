package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.ScheduleInfo;

import java.util.Optional;

public interface ScheduleReader {
    Optional<ScheduleInfo> get(String id);
}
