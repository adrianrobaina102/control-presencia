package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.adapters.ActiveMQEventStore;
import software.ulpgc.controlpresencia.factories.TrackingEventFactory;
import software.ulpgc.controlpresencia.model.*;
import software.ulpgc.controlpresencia.ports.EventStore;

public class Main {
    static void main() throws Exception {
        EventStore store = new ActiveMQEventStore("fichajes.queue");
        TrackingEvent event = TrackingEventFactory.createTrackingEvent();
        store.register(event);
        ((ActiveMQEventStore) store).close();
    }
}
