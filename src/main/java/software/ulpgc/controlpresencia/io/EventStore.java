package software.ulpgc.controlpresencia.io;

import software.ulpgc.controlpresencia.model.TrackingEvent;

public interface EventStore {
    void register(TrackingEvent event);
}
