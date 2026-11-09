package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;

public interface EventPublisher {
    void publish(TrackingEvent event);
}
