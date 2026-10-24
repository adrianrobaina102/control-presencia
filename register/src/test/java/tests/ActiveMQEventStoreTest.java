package tests;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.adapters.ActiveMQEventStore;
import software.ulpgc.controlpresencia.model.*;
import software.ulpgc.controlpresencia.ports.EventStore;

import javax.jms.*;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ActiveMQEventStoreTest {

    private static final String TEST_QUEUE = "fichajes.test.queue";

    @Test
    public void should_send_tracking_event_to_activemq_queue() throws Exception {
        EventStore store = new ActiveMQEventStore(TEST_QUEUE);
        store.register(createSampleTrackingEvent());
        ((ActiveMQEventStore) store).close();

        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try (Connection connection = factory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                Destination destination = session.createQueue(TEST_QUEUE);
                try (MessageConsumer consumer = session.createConsumer(destination)) {

                    Message message = consumer.receive(2000);

                    assertThat(message).isNotNull();
                    assertThat(message).isInstanceOf(TextMessage.class);

                    String text = ((TextMessage) message).getText();
                    assertThat(text).contains("emp-01")
                            .contains("Sede Central")
                            .contains("In");
                }
            }
        }
    }

    private TrackingEvent createSampleTrackingEvent() {
        Contact contact = new Contact("600000000", "adrian@ulpgc.es", LocalDate.of(2026, 1, 1));
        Employee employee = new Employee("emp-01", "Adrián", contact);
        Place place = new Place("hq-01", "Sede Central");
        Instant timestamp = Instant.parse("2026-10-13T08:30:00Z");

        return new TrackingEvent(timestamp, employee, place, TrackingType.In);
    }
}