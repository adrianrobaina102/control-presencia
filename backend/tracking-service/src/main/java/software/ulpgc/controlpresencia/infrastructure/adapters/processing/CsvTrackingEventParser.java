package software.ulpgc.controlpresencia.infrastructure.adapters.processing;

import software.ulpgc.controlpresencia.architecture.domain.Employee;
import software.ulpgc.controlpresencia.architecture.domain.Place;
import software.ulpgc.controlpresencia.architecture.domain.TrackingEvent;
import software.ulpgc.controlpresencia.architecture.domain.TrackingType;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventParser;

import java.time.Instant;

public class CsvTrackingEventParser implements TrackingEventParser {
    @Override
    public TrackingEvent parse(String input) {
        return parse(input.split(";"));
    }

    private TrackingEvent parse(String[] split) {
        return new TrackingEvent(new Employee(split[0]), new Place(split[1]), TrackingType.valueOf(split[2]));
    }
}
