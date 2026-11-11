package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.GetScheduleUseCase;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GetScheduleUseCaseTest {

    @Test
    public void should_return_schedule_when_schedule_exists() {
        Schedule sampleSchedule = createSampleSchedule("SCH-OFFICE-CONTINUOUS", "Oficina Tecnologica");
        ScheduleReader mockReader = new ScheduleReader() {
            @Override
            public Optional<Schedule> get(String id) {
                return "SCH-OFFICE-CONTINUOUS".equals(id) ? Optional.of(sampleSchedule) : Optional.empty();
            }

            @Override
            public List<Schedule> getAll() {
                return List.of(sampleSchedule);
            }
        };

        GetScheduleUseCase useCase = new GetScheduleUseCase(mockReader);
        Optional<Schedule> result = useCase.execute("SCH-OFFICE-CONTINUOUS");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Oficina Tecnologica");
        assertThat(result.get().type()).isEqualTo(ShiftType.CONTINUOUS);
        assertThat(result.get().courtesyMinutes()).isEqualTo(10);
        assertThat(result.get().totalExpectedMinutes()).isEqualTo(480);
        assertThat(result.get().appliesOn(DayOfWeek.MONDAY)).isTrue();
        assertThat(result.get().appliesOn(DayOfWeek.SUNDAY)).isFalse();
    }

    @Test
    public void should_return_empty_optional_when_schedule_does_not_exist() {
        ScheduleReader emptyReader = new ScheduleReader() {
            @Override
            public Optional<Schedule> get(String id) {
                return Optional.empty();
            }

            @Override
            public List<Schedule> getAll() {
                return List.of();
            }
        };

        GetScheduleUseCase useCase = new GetScheduleUseCase(emptyReader);
        Optional<Schedule> result = useCase.execute("non-existent");

        assertThat(result).isEmpty();
    }

    private Schedule createSampleSchedule(String id, String name) {
        TimeInterval interval = new TimeInterval(LocalTime.of(8, 0), LocalTime.of(16, 0));
        List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        return new Schedule(id, name, 10, 5, List.of(interval), ShiftType.CONTINUOUS, days);
    }
}
