package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.ports.EventRepository;

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
