package software.ulpgc.controlpresencia.application.ports.processing;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;

public interface TrackingEventSerializer {
    String serialize(TrackingEvent event);
}
