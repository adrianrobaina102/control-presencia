package software.ulpgc.controlpresencia.application.usecases;

import software.ulpgc.controlpresencia.application.ports.EventRepository;

public class StoreEventUseCase implements UseCase<String, Void> {
    private final EventRepository  eventRepository;

    public StoreEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Void execute(String eventText) {
        this.eventRepository.save(eventText);
        return null;
    }
}
