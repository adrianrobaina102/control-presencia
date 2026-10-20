package software.ulpgc.controlpresencia.model;

import java.time.Instant;

public record TrackingEvent (Instant ts, Employee employee, Place place, TrackingType type) {
}
