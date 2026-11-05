package software.ulpgc.controlpresencia.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

public class ActiveMQBrokerConnection implements AutoCloseable {
    private final Connection connection;
    private final Session session;

    public ActiveMQBrokerConnection() throws JMSException {
        ActiveMQConnectionFactory broker = new ActiveMQConnectionFactory("tcp://localhost:61616");
        this.connection = broker.createConnection();
        this.connection.start();

        this.session = this.connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
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
