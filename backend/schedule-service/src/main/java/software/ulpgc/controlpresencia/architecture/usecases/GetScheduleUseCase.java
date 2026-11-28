package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.util.Optional;

public class GetScheduleUseCase implements UseCase<String, Output<Schedule>> {
    private final ScheduleReader scheduleReader;

    public GetScheduleUseCase(ScheduleReader scheduleReader) {
        this.scheduleReader = scheduleReader;
    }


    @Override
    public Output<Schedule> execute(String id) {
        Optional<Schedule> schedule = this.scheduleReader.get(id);
        return schedule.map(Output::ok).orElseGet(() -> Output.notFound("Schedule not found"));
    }
}
