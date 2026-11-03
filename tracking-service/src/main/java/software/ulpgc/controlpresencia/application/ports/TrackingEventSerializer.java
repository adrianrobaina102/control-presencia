package software.ulpgc.controlpresencia.application.ports;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;

public interface TrackingEventSerializer {
    String serialize(TrackingEvent event);
}
