package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.util.List;

public class ListSchedulesUseCase implements UseCase<Void, Output<List<Schedule>>> {
    private final ScheduleReader scheduleReader;

    public ListSchedulesUseCase(ScheduleReader scheduleReader) {
        this.scheduleReader = scheduleReader;
    }

    @Override
    public Output<List<Schedule>> execute(Void input) {
        List<Schedule> all = this.scheduleReader.getAll();
        return Output.ok(all);
    }
}
