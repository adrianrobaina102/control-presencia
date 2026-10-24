package software.ulpgc.controlpresencia.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;
import software.ulpgc.controlpresencia.ports.EventReceiver;

import javax.jms.*;

public class ActiveMQEventReceiver implements EventReceiver {
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    public ActiveMQEventReceiver(String queueName) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        this.connection = factory.createConnection();
        this.connection.start();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = this.session.createQueue(queueName);
        this.consumer = session.createConsumer(destination);
    }
    @Override
    public String receive()  {
        try {
            Message message = this.consumer.receive();
            return ((TextMessage) message).getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    public void close()
    {
        try {
            this.connection.close();
        } catch (JMSException e) {}
    }
}
