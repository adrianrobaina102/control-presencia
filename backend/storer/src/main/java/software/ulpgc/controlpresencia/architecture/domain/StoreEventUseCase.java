package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.ports.EventWriter;

public class StoreEventUseCase implements UseCase<String, Void> {
    private final EventWriter eventWriter;

    public StoreEventUseCase(EventWriter eventWriter) {
        this.eventWriter = eventWriter;
    }

    @Override
    public Void execute(String eventText) {
        this.eventWriter.write(eventText);
        return null;
    }
}
