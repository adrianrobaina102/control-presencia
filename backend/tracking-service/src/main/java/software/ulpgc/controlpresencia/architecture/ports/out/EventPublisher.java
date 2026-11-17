package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.domain.TrackingEvent;

public interface EventPublisher {
    void publish(TrackingEvent event);
}
