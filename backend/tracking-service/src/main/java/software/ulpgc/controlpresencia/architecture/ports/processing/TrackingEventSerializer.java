package software.ulpgc.controlpresencia.architecture.ports.processing;

import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;

public interface TrackingEventSerializer {
    String serialize(TrackingEvent event);
}
