package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.architecture.ports.out.EmployeeReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventReader;
import software.ulpgc.controlpresencia.architecture.ports.out.ScheduleReader;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateDailyReportUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateDailyReportUseCaseTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 2);

    @Test
    public void should_generate_daily_report_when_employee_and_schedule_exist() {
        EmployeeReader employeeReader = id -> CompletableFuture.completedFuture(
                "emp-01".equals(id) ? Optional.of(new EmployeeInfo("emp-01", "Adrián", "SCH-01")) : Optional.empty()
        );
        ScheduleReader scheduleReader = id -> CompletableFuture.completedFuture(
                "SCH-01".equals(id)
                        ? Optional.of(new ScheduleInfo("SCH-01", "Oficina", 10, 5,
                        List.of(new TimeInterval(LocalTime.of(8, 0), LocalTime.of(16, 0))),
                        List.of(DayOfWeek.MONDAY)))
                        : Optional.empty()
        );
        EventReader eventReader = (_, _) -> CompletableFuture.completedFuture(List.of(
                new TrackingInfo(instant(8, 0), "emp-01", "Oficina", TrackingType.In),
                new TrackingInfo(instant(16, 0), "emp-01", "Oficina", TrackingType.Out)
        ));

        GenerateDailyReportUseCase useCase = new GenerateDailyReportUseCase(employeeReader, eventReader, scheduleReader);
        Output<DailyReport> output = useCase.execute(new GenerateDailyReportUseCase.Param("emp-01", DATE)).join();

        assertThat(output.isSuccess()).isTrue();
        assertThat(output.status()).isEqualTo(200);
        assertThat(output.result().expectedMinutes()).isEqualTo(480);
        assertThat(output.result().actualMinutes()).isEqualTo(480);
        assertThat(output.result().incidents()).isEmpty();
    }

    @Test
    public void should_return_not_found_when_employee_does_not_exist() {
        GenerateDailyReportUseCase useCase = new GenerateDailyReportUseCase(
                _ -> CompletableFuture.completedFuture(Optional.empty()),
                (_, _) -> CompletableFuture.completedFuture(List.of()),
                _ -> CompletableFuture.completedFuture(Optional.empty())
        );

        Output<DailyReport> output = useCase.execute(new GenerateDailyReportUseCase.Param("unknown", DATE)).join();

        assertThat(output.isSuccess()).isFalse();
        assertThat(output.status()).isEqualTo(404);
    }

    @Test
    public void should_return_not_found_when_schedule_does_not_exist() {
        EmployeeReader employeeReader = _ -> CompletableFuture.completedFuture(Optional.of(new EmployeeInfo("emp-01", "Adrián", "SCH-MISSING")));
        GenerateDailyReportUseCase useCase = new GenerateDailyReportUseCase(
                employeeReader,
                (_, _) -> CompletableFuture.completedFuture(List.of()),
                _ -> CompletableFuture.completedFuture(Optional.empty())
        );

        Output<DailyReport> output = useCase.execute(new GenerateDailyReportUseCase.Param("emp-01", DATE)).join();

        assertThat(output.isSuccess()).isFalse();
        assertThat(output.status()).isEqualTo(404);
    }

    private Instant instant(int hour, int minute) {
        return LocalDateTime.of(DATE, LocalTime.of(hour, minute)).atZone(ZoneId.systemDefault()).toInstant();
    }
}
