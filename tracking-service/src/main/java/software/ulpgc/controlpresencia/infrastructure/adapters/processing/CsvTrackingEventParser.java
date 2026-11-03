package software.ulpgc.controlpresencia.infrastructure.adapters.processing;

import software.ulpgc.controlpresencia.application.model.Employee;
import software.ulpgc.controlpresencia.application.model.Place;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.model.TrackingType;
import software.ulpgc.controlpresencia.application.ports.processing.TrackingEventParser;

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
