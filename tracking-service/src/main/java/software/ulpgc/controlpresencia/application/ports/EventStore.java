package software.ulpgc.controlpresencia.application.ports;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;
public interface EventStore {
    void register(TrackingEvent event);
}
