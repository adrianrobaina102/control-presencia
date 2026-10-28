package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventReceiver;
import software.ulpgc.controlpresencia.infrastructure.adapters.FileEventRepository;
import software.ulpgc.controlpresencia.application.ports.EventReceiver;
import software.ulpgc.controlpresencia.application.ports.EventRepository;

import javax.jms.JMSException;
import java.io.File;

public class Main {
    public static void main() throws Exception {
        ActiveMQBrokerConnection connection = new ActiveMQBrokerConnection();
        EventReceiver receiver = new ActiveMQEventReceiver(connection.getSession(), "fichajes.queue");
        EventRepository repository = new FileEventRepository(new File("store"));

        System.out.println("Storer encendido. Escuchando nuevos fichajes...");
        String message = receiver.receive();
        System.out.println("Message received: " + message );
        repository.save(message);
        connection.close();
    }
}
