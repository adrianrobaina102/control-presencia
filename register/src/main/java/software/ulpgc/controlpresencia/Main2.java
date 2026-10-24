package software.ulpgc.controlpresencia;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Main2 {
    static void main() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("fichajes.queue");

        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("¡Primer fichaje enviado desde Java!");
        producer.send(message);

        System.out.println("Mensaje enviado al broker con éxito.");
        connection.close();

    }
}
