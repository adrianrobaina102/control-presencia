package software.ulpgc.controlpresencia.architecture.ports.processing;

import software.ulpgc.controlpresencia.architecture.domain.TrackingEvent;

public interface TrackingEventSerializer {
    String serialize(TrackingEvent event);
}
