package software.ulpgc.controlpresencia.ports;

import software.ulpgc.controlpresencia.model.TrackingEvent;

import javax.jms.JMSException;

public interface EventStore {
    void register(TrackingEvent event) throws JMSException;
}
