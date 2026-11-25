package software.ulpgc.controlpresencia.architecture.ports.in;

import software.ulpgc.controlpresencia.architecture.model.Schedule;

import java.util.Optional;

public interface ScheduleReader {
    Optional<Schedule> get(String id);
}
