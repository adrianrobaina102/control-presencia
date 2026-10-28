package software.ulpgc.controlpresencia.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.ports.EventStore;

import javax.jms.*;

public class ActiveMQEventStore implements EventStore
{
    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public ActiveMQEventStore(String queueName) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        this.connection = factory.createConnection();
        this.connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination  = session.createQueue(queueName);
        this.producer = session.createProducer(destination);
    }

    @Override
    public void register(TrackingEvent event) {
        try {
            producer.send(session.createTextMessage(serializa(event)));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    public void close() throws Exception {
        this.connection.close();
    }
    private String serializa(TrackingEvent event) {
        return event.ts() + ";" + event.employee().id() + ";" +
                event.place().name() + ";" + event.type();
    }
}
