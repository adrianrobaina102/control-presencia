package software.ulpgc.controlpresencia.architecture.ports.in;

import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;

import java.time.LocalDate;
import java.util.List;

public interface EventReader {
    List<TrackingEvent> getEvents(String employeeId, LocalDate date);
}
