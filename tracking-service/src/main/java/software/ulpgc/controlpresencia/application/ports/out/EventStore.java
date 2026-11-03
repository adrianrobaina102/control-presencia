package software.ulpgc.controlpresencia.application.ports.out;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;
public interface EventStore {
    void register(TrackingEvent event);
}
