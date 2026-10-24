package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.adapters.ActiveMQEventStore;
import software.ulpgc.controlpresencia.model.*;
import software.ulpgc.controlpresencia.ports.EventStore;

import javax.jms.*;
import java.time.Instant;
import java.time.LocalDate;

public class Main2 {
    static void main() throws Exception {
        Contact contact = new Contact("666555444", "adri@correo.com", LocalDate.now());
        Employee employee = new Employee("EMP-001", "Adri", contact);
        Place place = new Place("PLC-01", "Oficina Central");

        TrackingEvent trackingEvent = new TrackingEvent(Instant.now(), employee, place, TrackingType.In);

        EventStore store = new ActiveMQEventStore("fichajes.queue");
        store.register(trackingEvent);
        ((ActiveMQEventStore) store).close();
    }
}
