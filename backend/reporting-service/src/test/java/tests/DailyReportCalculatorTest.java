package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.DailyReportCalculator;
import software.ulpgc.controlpresencia.architecture.model.*;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DailyReportCalculatorTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 11, 8);

    @Test
    public void should_calculate_punctual_day_without_incidents() {
        ScheduleInfo schedule = officeSchedule();
        List<TrackingInfo> events = List.of(
                event(MONDAY, 8, 5, TrackingType.In),
                event(MONDAY, 16, 5, TrackingType.Out)
        );

        DailyReport report = DailyReportCalculator.calculate("emp-01", MONDAY, schedule, events);

        assertThat(report.expectedMinutes()).isEqualTo(480);
        assertThat(report.actualMinutes()).isEqualTo(480);
        assertThat(report.balanceMinutes()).isEqualTo(0);
        assertThat(report.incidents()).isEmpty();
    }

    @Test
    public void should_detect_late_arrival_beyond_courtesy_margin() {
        ScheduleInfo schedule = officeSchedule();
        List<TrackingInfo> events = List.of(
                event(MONDAY, 8, 25, TrackingType.In),
                event(MONDAY, 16, 25, TrackingType.Out)
        );

        DailyReport report = DailyReportCalculator.calculate("emp-01", MONDAY, schedule, events);

        assertThat(report.incidents())
                .hasSize(1)
                .first()
                .satisfies(incident -> {
                    assertThat(incident.type()).isEqualTo(IncidentType.LATE_ARRIVAL);
                    assertThat(incident.minutes()).isEqualTo(25);
                });
    }

    @Test
    public void should_detect_early_exit_before_exit_margin() {
        ScheduleInfo schedule = officeSchedule();
        List<TrackingInfo> events = List.of(
                event(MONDAY, 8, 0, TrackingType.In),
                event(MONDAY, 15, 30, TrackingType.Out)
        );

        DailyReport report = DailyReportCalculator.calculate("emp-01", MONDAY, schedule, events);

        assertThat(report.actualMinutes()).isEqualTo(450);
        assertThat(report.balanceMinutes()).isEqualTo(-30);
        assertThat(report.incidents())
                .hasSize(1)
                .first()
                .satisfies(incident -> {
                    assertThat(incident.type()).isEqualTo(IncidentType.EARLY_EXIT);
                    assertThat(incident.minutes()).isEqualTo(30);
                });
    }

    @Test
    public void should_detect_overtime_when_actual_exceeds_expected() {
        ScheduleInfo schedule = officeSchedule();
        List<TrackingInfo> events = List.of(
                event(MONDAY, 8, 0, TrackingType.In),
                event(MONDAY, 17, 0, TrackingType.Out)
        );

        DailyReport report = DailyReportCalculator.calculate("emp-01", MONDAY, schedule, events);

        assertThat(report.actualMinutes()).isEqualTo(540);
        assertThat(report.balanceMinutes()).isEqualTo(60);
        assertThat(report.incidents())
                .hasSize(1)
                .first()
                .satisfies(incident -> {
                    assertThat(incident.type()).isEqualTo(IncidentType.OVERTIME);
                    assertThat(incident.minutes()).isEqualTo(60);
                });
    }

    @Test
    public void should_detect_unjustified_absence_when_no_events_on_working_day() {
        DailyReport report = DailyReportCalculator.calculate("emp-01", MONDAY, officeSchedule(), List.of());

        assertThat(report.expectedMinutes()).isEqualTo(480);
        assertThat(report.actualMinutes()).isEqualTo(0);
        assertThat(report.incidents())
                .hasSize(1)
                .first()
                .satisfies(incident -> {
                    assertThat(incident.type()).isEqualTo(IncidentType.UNJUSTIFIED_ABSENCE);
                    assertThat(incident.minutes()).isEqualTo(480);
                });
    }

    @Test
    public void should_return_zero_expected_minutes_and_no_incidents_on_non_working_day() {
        DailyReport report = DailyReportCalculator.calculate("emp-01", SUNDAY, officeSchedule(), List.of());

        assertThat(report.expectedMinutes()).isEqualTo(0);
        assertThat(report.actualMinutes()).isEqualTo(0);
        assertThat(report.incidents()).isEmpty();
    }

    private ScheduleInfo officeSchedule() {
        return new ScheduleInfo(
                "SCH-OFFICE",
                "Oficina",
                10,
                5,
                List.of(new TimeInterval(LocalTime.of(8, 0), LocalTime.of(16, 0))),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
        );
    }

    private TrackingInfo event(LocalDate date, int hour, int minute, TrackingType type) {
        Instant ts = LocalDateTime.of(date, LocalTime.of(hour, minute)).atZone(ZoneId.systemDefault()).toInstant();
        return new TrackingInfo(ts, "emp-01", "Oficina Central", type);
    }
}
