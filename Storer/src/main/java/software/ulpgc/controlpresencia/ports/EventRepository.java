package software.ulpgc.controlpresencia.ports;

import javax.jms.JMSException;

public interface EventRepository {
    void save(String eventText) throws JMSException;
}
