package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.ListSchedulesUseCase;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ListSchedulesUseCaseTest {

    @Test
    public void should_return_all_schedules() {
        Schedule sched1 = createSampleSchedule("SCH-01", "Oficina", ShiftType.CONTINUOUS);
        Schedule sched2 = createSampleSchedule("SCH-02", "Comercio", ShiftType.SPLIT);

        ScheduleReader mockReader = new ScheduleReader() {
            @Override
            public Optional<Schedule> get(String id) {
                return Optional.empty();
            }

            @Override
            public List<Schedule> getAll() {
                return List.of(sched1, sched2);
            }
        };

        ListSchedulesUseCase useCase = new ListSchedulesUseCase(mockReader);
        List<Schedule> result = useCase.execute();

        assertThat(result).hasSize(2)
                .extracting(Schedule::name)
                .containsExactly("Oficina", "Comercio");
    }

    private Schedule createSampleSchedule(String id, String name, ShiftType type) {
        TimeInterval interval = new TimeInterval(LocalTime.of(9, 0), LocalTime.of(17, 0));
        return new Schedule(id, name, 10, 5, List.of(interval), type, List.of(DayOfWeek.MONDAY));
    }
}
