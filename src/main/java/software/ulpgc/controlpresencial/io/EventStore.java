package software.ulpgc.controlpresencial.io;

import software.ulpgc.controlpresencial.model.TrackingEvent;

public interface EventStore {
    void register(TrackingEvent event);
}
