package software.ulpgc.controlpresencia.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.ports.EventStore;

import javax.jms.*;

public class ActiveMQEventStore implements EventStore
{
    private final MessageProducer producer;
    private final Session session;
    public ActiveMQEventStore(Session session, String queueName) throws JMSException {
        this.session = session;
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
    private String serializa(TrackingEvent event) {
        return event.ts() + ";" + event.employee().id() + ";" +
                event.place().name() + ";" + event.type();
    }
}
