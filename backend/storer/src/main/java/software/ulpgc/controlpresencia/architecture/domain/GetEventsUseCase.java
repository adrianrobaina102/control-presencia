package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.ports.EventReader;

import java.util.List;

public class GetEventsUseCase implements UseCase<Void, List<String>> {
    private final EventReader eventReader;

    public GetEventsUseCase(EventReader eventReader) {
        this.eventReader = eventReader;
    }

    @Override
    public List<String> execute(Void input) {
        return this.eventReader.read();
    }
}
