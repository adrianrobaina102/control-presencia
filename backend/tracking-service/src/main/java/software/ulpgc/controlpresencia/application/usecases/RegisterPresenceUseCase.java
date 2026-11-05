package software.ulpgc.controlpresencia.application.usecases;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.ports.in.DeviceReader;
import software.ulpgc.controlpresencia.application.ports.out.EventStore;
import software.ulpgc.controlpresencia.application.ports.processing.TrackingEventParser;

public class RegisterPresenceUseCase implements UseCase<Void, Void> {
    private final EventStore eventStore;
    private final DeviceReader reader;
    private final TrackingEventParser parser;
    public RegisterPresenceUseCase(EventStore eventStore, DeviceReader reader, TrackingEventParser parser) {
        this.eventStore = eventStore;
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public Void execute(Void input) {
        String read = this.reader.read();
        TrackingEvent parse = this.parser.parse(read);
        this.eventStore.register(parse);
        return null;
    }
}
