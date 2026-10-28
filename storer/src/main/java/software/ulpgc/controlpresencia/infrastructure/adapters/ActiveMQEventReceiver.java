package software.ulpgc.controlpresencia.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;
import software.ulpgc.controlpresencia.application.ports.EventReceiver;

import javax.jms.*;

public class ActiveMQEventReceiver implements EventReceiver {
    private Session session;
    private MessageConsumer consumer;
    public ActiveMQEventReceiver(Session session, String queueName) throws JMSException {
        this.session = session;
        Destination destination = this.session.createQueue(queueName);
        this.consumer = session.createConsumer(destination);
    }
    @Override
    public String receive()  {
        try {
            Message message = this.consumer.receive();
            String text = ((TextMessage) message).getText();

            message.acknowledge();
            return text;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
