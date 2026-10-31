package software.ulpgc.controlpresencia.application.model;

import java.time.Instant;

public record TrackingEvent (Instant ts, Employee employee, Place place, TrackingType type) {
    public TrackingEvent(Employee employee, Place place, TrackingType type) {
        this(Instant.now(), employee, place, type);
    }
}
