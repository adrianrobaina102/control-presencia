package software.ulpgc.controlpresencia.application.ports;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;

public interface TrackingEventParser {
    TrackingEvent parse(String input);
}
