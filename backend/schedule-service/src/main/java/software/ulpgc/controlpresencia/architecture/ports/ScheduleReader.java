package software.ulpgc.controlpresencia.architecture.ports;

import software.ulpgc.controlpresencia.architecture.model.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleReader {
    Optional<Schedule> get(String id);
    List<Schedule> getAll();
}
