package tests;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventReceiver;
import software.ulpgc.controlpresencia.application.ports.EventReceiver;

import javax.jms.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ActiveMQEventReceiverTest {

    private static final String TEST_QUEUE = "fichajes.test.receiver.queue";

    @Test
    public void should_receive_tracking_event_from_activemq_queue() throws Exception {
        String expectedMessage = "emp-01,Sede Central,In,2026-10-13T08:30:00Z";
        sendMessageToQueue(TEST_QUEUE, expectedMessage);

        try (ActiveMQBrokerConnection brokerConnection = new ActiveMQBrokerConnection()) {
            EventReceiver receiver = new ActiveMQEventReceiver(brokerConnection.getSession(), TEST_QUEUE);
            String actualMessage = receiver.receive();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }
    }

    private void sendMessageToQueue(String queueName, String text) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try (Connection connection = factory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                Destination destination = session.createQueue(queueName);
                try (MessageProducer producer = session.createProducer(destination)) {
                    producer.send(session.createTextMessage(text));
                }
            }
        }
    }
}
