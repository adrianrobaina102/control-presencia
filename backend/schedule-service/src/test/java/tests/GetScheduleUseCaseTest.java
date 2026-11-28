package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.usecases.GetScheduleUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;
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
        Output<Schedule> output = useCase.execute("SCH-OFFICE-CONTINUOUS");

        assertThat(output.isSuccess()).isTrue();
        assertThat(output.status()).isEqualTo(200);
        assertThat(output.result().name()).isEqualTo("Oficina Tecnologica");
        assertThat(output.result().type()).isEqualTo(ShiftType.CONTINUOUS);
        assertThat(output.result().courtesyMinutes()).isEqualTo(10);
        assertThat(output.result().totalExpectedMinutes()).isEqualTo(480);
        assertThat(output.result().appliesOn(DayOfWeek.MONDAY)).isTrue();
        assertThat(output.result().appliesOn(DayOfWeek.SUNDAY)).isFalse();
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
        Output<Schedule> output = useCase.execute("non-existent");

        assertThat(output.isSuccess()).isFalse();
        assertThat(output.status()).isEqualTo(404);
        assertThat(output.error()).isEqualTo("Schedule not found");
    }

    private Schedule createSampleSchedule(String id, String name) {
        return Schedule.builder()
                .id(id)
                .name(name)
                .courtesyMinutes(10)
                .exitMarginMinutes(5)
                .intervals(List.of(new TimeInterval(LocalTime.of(8, 0), LocalTime.of(16, 0))))
                .type(ShiftType.CONTINUOUS)
                .days(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
                .build();
    }
}
