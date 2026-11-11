package software.ulpgc.controlpresencia.architecture.model;

import java.time.Duration;
import java.time.LocalTime;

public record TimeInterval(LocalTime start, LocalTime end) {
    public int durationMinutes(){
        return Math.toIntExact(Math.abs(Duration.between(start, end).toMinutes()));
    }
}
