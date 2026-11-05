package software.ulpgc.controlpresencia.application.usecases;

import software.ulpgc.controlpresencia.application.ports.EventReader;

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
