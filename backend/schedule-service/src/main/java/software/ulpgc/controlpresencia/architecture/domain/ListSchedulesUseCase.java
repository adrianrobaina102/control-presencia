package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.util.List;

public class ListSchedulesUseCase implements UseCase<Void, List<Schedule>> {
    private final ScheduleReader scheduleReader;

    public ListSchedulesUseCase(ScheduleReader scheduleReader) {
        this.scheduleReader = scheduleReader;
    }

    @Override
    public List<Schedule> execute(Void input) {
        return this.scheduleReader.getAll();
    }
}
