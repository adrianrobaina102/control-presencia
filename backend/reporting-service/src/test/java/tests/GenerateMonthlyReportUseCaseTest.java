package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.architecture.ports.out.EmployeeReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventReader;
import software.ulpgc.controlpresencia.architecture.ports.out.ScheduleReader;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateDailyReportUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateMonthlyReportUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateMonthlyReportUseCaseTest {

    @Test
    public void should_aggregate_monthly_hours_delays_and_absences() {
        LocalDate day1 = LocalDate.of(2026, 11, 2); // Monday
        LocalDate day2 = LocalDate.of(2026, 11, 3); // Tuesday

        EmployeeReader employeeReader = _ -> CompletableFuture.completedFuture(Optional.of(new EmployeeInfo("emp-01", "Adrián", "SCH-01")));
        ScheduleReader scheduleReader = _ -> CompletableFuture.completedFuture(Optional.of(new ScheduleInfo(
                "SCH-01",
                "Oficina",
                10,
                5,
                List.of(new TimeInterval(LocalTime.of(8, 0), LocalTime.of(16, 0))),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
        )));
        EventReader eventReader = (_, date) -> {
            if (date.equals(day1)) {
                return CompletableFuture.completedFuture(List.of(
                        event(day1, 8, 0, TrackingType.In),
                        event(day1, 16, 0, TrackingType.Out)
                ));
            }
            if (date.equals(day2)) {
                return CompletableFuture.completedFuture(List.of(
                        event(day2, 8, 20, TrackingType.In),
                        event(day2, 16, 20, TrackingType.Out)
                ));
            }
            return CompletableFuture.completedFuture(List.of());
        };

        GenerateDailyReportUseCase dailyUseCase = new GenerateDailyReportUseCase(employeeReader, eventReader, scheduleReader);
        GenerateMonthlyReportUseCase monthlyUseCase = new GenerateMonthlyReportUseCase(dailyUseCase);

        Output<MonthlyReport> output = monthlyUseCase.execute(new GenerateMonthlyReportUseCase.Param("emp-01", YearMonth.of(2026, 11))).join();

        assertThat(output.isSuccess()).isTrue();
        assertThat(output.status()).isEqualTo(200);
        MonthlyReport report = output.result();
        assertThat(report.employeeId()).isEqualTo("emp-01");
        assertThat(report.yearMonth()).isEqualTo("2026-11");
        assertThat(report.reportList()).hasSize(30);
        // Nov 2026 has 5 Mondays + 4 Tuesdays = 9 working days * 8h = 72h expected
        assertThat(report.totalExpectedHours()).isEqualTo(72.0);
        assertThat(report.totalActualHours()).isEqualTo(16.0);
        assertThat(report.totalDelayMinutes()).isEqualTo(20);
        assertThat(report.daysAbsent()).isEqualTo(7);
    }

    @Test
    public void should_return_not_found_when_employee_does_not_exist() {
        GenerateDailyReportUseCase dailyUseCase = new GenerateDailyReportUseCase(
                _ -> CompletableFuture.completedFuture(Optional.empty()),
                (_, _) -> CompletableFuture.completedFuture(List.of()),
                _ -> CompletableFuture.completedFuture(Optional.empty())
        );
        GenerateMonthlyReportUseCase monthlyUseCase = new GenerateMonthlyReportUseCase(dailyUseCase);

        Output<MonthlyReport> output = monthlyUseCase.execute(new GenerateMonthlyReportUseCase.Param("unknown", YearMonth.of(2026, 11))).join();

        assertThat(output.isSuccess()).isFalse();
        assertThat(output.status()).isEqualTo(404);
    }

    private TrackingInfo event(LocalDate date, int hour, int minute, TrackingType type) {
        Instant ts = LocalDateTime.of(date, LocalTime.of(hour, minute)).atZone(ZoneId.systemDefault()).toInstant();
        return new TrackingInfo(ts, "emp-01", "Oficina", type);
    }
}
