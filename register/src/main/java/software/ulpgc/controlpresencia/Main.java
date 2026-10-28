package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventStore;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.factories.TrackingEventFactory;
import software.ulpgc.controlpresencia.application.ports.EventStore;

public class Main {
    static void main() throws Exception {
        EventStore store = new ActiveMQEventStore("fichajes.queue");
        TrackingEvent event = TrackingEventFactory.createTrackingEvent();
        store.register(event);
        ((ActiveMQEventStore) store).close();
    }
}
