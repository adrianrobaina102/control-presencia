package software.ulpgc.controlpresencia.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

public class ActiveMQBrokerConnection implements AutoCloseable {
    private final Connection connection;
    private final Session session;

    public ActiveMQBrokerConnection() throws JMSException {
        this("tcp://localhost:61616");
    }

    public ActiveMQBrokerConnection(String brokerUrl) throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = factory.createConnection();
        this.connection.start();

        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public Session getSession() {
        return this.session;
    }

    @Override
    public void close() throws Exception {
        this.session.close();
        this.connection.close();
    }
}
