package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.adapters.ActiveMQEventReceiver;
import software.ulpgc.controlpresencia.adapters.FileEventRepository;
import software.ulpgc.controlpresencia.ports.EventReceiver;
import software.ulpgc.controlpresencia.ports.EventRepository;

import javax.jms.JMSException;
import java.io.File;

public class Main {
    public static void main() throws JMSException {
        EventReceiver receiver = new ActiveMQEventReceiver("fichajes.queue");
        EventRepository repository = new FileEventRepository(new File("store"));

        System.out.println("Storer encendido. Escuchando nuevos fichajes...");
        String message = receiver.receive();
        System.out.println("Message received: " + message );
        repository.save(message);
        ((ActiveMQEventReceiver) receiver ).close();
    }
}
