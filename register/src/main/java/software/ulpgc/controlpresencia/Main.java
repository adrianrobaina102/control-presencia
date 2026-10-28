package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventStore;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.factories.TrackingEventFactory;
import software.ulpgc.controlpresencia.application.ports.EventStore;

public class Main {
    static void main() throws Exception {
        ActiveMQBrokerConnection brokerConnection = new ActiveMQBrokerConnection();
        EventStore store = new ActiveMQEventStore(brokerConnection.getSession(), "fichajes.queue");
        TrackingEvent event = TrackingEventFactory.createTrackingEvent();
        store.register(event);
        brokerConnection.close();
    }
}
