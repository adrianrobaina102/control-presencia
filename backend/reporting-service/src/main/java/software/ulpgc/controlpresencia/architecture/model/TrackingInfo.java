package software.ulpgc.controlpresencia.architecture.model;

import java.time.Instant;

public record TrackingInfo(Instant ts, String employeeId, String place, TrackingType type) {
}
