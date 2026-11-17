package software.ulpgc.controlpresencia.architecture.ports.processing;

import software.ulpgc.controlpresencia.architecture.domain.TrackingEvent;

public interface TrackingEventParser {
    TrackingEvent parse(String input);
}
