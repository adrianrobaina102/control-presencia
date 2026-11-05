package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.ports.out.EventStore;
import software.ulpgc.controlpresencia.application.ports.processing.TrackingEventSerializer;

import javax.jms.*;

public class ActiveMQEventStore implements EventStore
{
    private final MessageProducer producer;
    private final Session session;
    private final TrackingEventSerializer serializer;
    public ActiveMQEventStore(Session session, String queueName, TrackingEventSerializer serializer) throws JMSException {
        this.session = session;
        this.serializer = serializer;
        Destination destination  = session.createQueue(queueName);
        this.producer = session.createProducer(destination);
    }

    @Override
    public void register(TrackingEvent event) {
        try {
            producer.send(session.createTextMessage(serializer.serialize(event)));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }
}
