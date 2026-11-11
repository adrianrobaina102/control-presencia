package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.util.Optional;

public class GetScheduleUseCase implements UseCase<String, Optional<Schedule>> {
    private final ScheduleReader scheduleReader;

    public GetScheduleUseCase(ScheduleReader scheduleReader) {
        this.scheduleReader = scheduleReader;
    }


    @Override
    public Optional<Schedule> execute(String id) {
        return this.scheduleReader.get(id);
    }
}
