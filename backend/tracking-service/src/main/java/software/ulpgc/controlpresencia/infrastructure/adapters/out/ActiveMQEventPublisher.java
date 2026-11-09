package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;
import software.ulpgc.controlpresencia.architecture.ports.out.EventPublisher;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventSerializer;

import javax.jms.*;

public class ActiveMQEventPublisher implements EventPublisher {
    private final MessageProducer producer;
    private final Session session;
    private final TrackingEventSerializer serializer;

    public ActiveMQEventPublisher(Session session, String queueName, TrackingEventSerializer serializer) throws JMSException {
        this.session = session;
        this.serializer = serializer;
        Destination destination = session.createQueue(queueName);
        this.producer = session.createProducer(destination);
    }

    @Override
    public void publish(TrackingEvent event) {
        try {
            producer.send(session.createTextMessage(serializer.serialize(event)));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
