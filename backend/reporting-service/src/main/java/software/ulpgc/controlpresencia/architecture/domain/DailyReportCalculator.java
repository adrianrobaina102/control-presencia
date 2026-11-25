package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class DailyReportCalculator {

    public static DailyReport calculate(String employeeId, LocalDate date, ScheduleInfo schedule, List<TrackingInfo> events) {
        int expected = expectedMinutes(date, schedule);
        int actual = actualMinutes(events);
        return DailyReport.builder()
                .employeeId(employeeId)
                .date(date)
                .expectedMinutes(expected)
                .actualMinutes(actual)
                .balanceMinutes(actual - expected)
                .incidents(List.of())
                .build();
    }

    private static int expectedMinutes(LocalDate date, ScheduleInfo schedule) {
        return schedule.appliesOn(date.getDayOfWeek()) ? schedule.totalExpectedMinutes() : 0;
    }

    private static int actualMinutes(List<TrackingInfo> events) {
        if (events == null || events.isEmpty()) return 0;
        int total = 0;
        Instant lastIn = null;
        for (TrackingInfo event : sortedEvents(events)) {
            if (event.type() == TrackingType.In && lastIn == null) {
                lastIn = event.ts();
            } else if (event.type() == TrackingType.Out && lastIn != null) {
                total += (int) Duration.between(lastIn, event.ts()).toMinutes();
                lastIn = null;
            }
        }
        return total;
    }

    private static List<TrackingInfo> sortedEvents(List<TrackingInfo> events) {
        return events.stream()
                .sorted(Comparator.comparing(TrackingInfo::ts))
                .toList();
    }
}
