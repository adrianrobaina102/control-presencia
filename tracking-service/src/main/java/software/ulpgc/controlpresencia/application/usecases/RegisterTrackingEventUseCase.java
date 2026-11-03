package software.ulpgc.controlpresencia.application.usecases;

import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.ports.EventStore;

public class RegisterTrackingEventUseCase implements UseCase<TrackingEvent, Void>{
    private final EventStore eventStore;

    public RegisterTrackingEventUseCase(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Void execute(TrackingEvent input) {
        this.eventStore.register(input);
        return null;
    }
}
