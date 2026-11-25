package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.TrackingInfo;

import java.time.LocalDate;
import java.util.List;

public interface EventReader {
    List<TrackingInfo> getEvents(String employeeId, LocalDate date);
}
