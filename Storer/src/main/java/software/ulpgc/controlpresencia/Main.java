package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.adapters.ActiveMQEventReceiver;
import software.ulpgc.controlpresencia.ports.EventReceiver;

import javax.jms.JMSException;

public class Main {
    public static void main() throws JMSException {
        EventReceiver receiver = new ActiveMQEventReceiver("fichajes.queue");
        System.out.println("Message received: " +  receiver.receive());
        ((ActiveMQEventReceiver) receiver).close();
    }
}
