package software.ulpgc.controlpresencial.model;

import java.time.Instant;

public record TrackingEvent (Instant ts, Employee employee, Place place, TrackingEvent type) {
}
