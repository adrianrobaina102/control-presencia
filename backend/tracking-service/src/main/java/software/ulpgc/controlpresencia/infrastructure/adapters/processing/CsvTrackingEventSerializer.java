package software.ulpgc.controlpresencia.infrastructure.adapters.processing;

import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventSerializer;

public class CsvTrackingEventSerializer implements TrackingEventSerializer {
    @Override
    public String serialize(TrackingEvent event) {
        return event.ts() + ";" + event.employee().id() + ";" +
                event.place().name() + ";" + event.type();
    }
}
