package software.ulpgc.controlpresencia.ports;

import javax.jms.JMSException;

public interface EventReceiver {
    String receive() throws JMSException;
}
