package software.ulpgc.controlpresencia.ports;

import software.ulpgc.controlpresencia.model.TrackingEvent;

public interface EventStore {
    void register(TrackingEvent event);
}
