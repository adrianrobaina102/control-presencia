package software.ulpgc.controlpresencia.architecture.ports.processing;

import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;

public interface TrackingEventParser {
    TrackingEvent parse(String input);
}
