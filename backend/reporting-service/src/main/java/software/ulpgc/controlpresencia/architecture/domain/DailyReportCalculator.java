package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DailyReportCalculator {

    public static DailyReport calculate(String employeeId, LocalDate date, ScheduleInfo schedule, List<TrackingInfo> events) {
        int expectedMinutes = expectedMinutes(date, schedule);
        int actualMinutes = actualMinutes(events);
        List<Incident> incidents = detectIncidents(expectedMinutes, actualMinutes, schedule, events);
        return DailyReport.builder()
                .employeeId(employeeId)
                .date(date)
                .expectedMinutes(expectedMinutes)
                .actualMinutes(actualMinutes)
                .balanceMinutes(actualMinutes - expectedMinutes)
                .incidents(incidents)
                .build();
    }

    private static List<Incident> detectIncidents(int expectedMinutes, int actualMinutes, ScheduleInfo schedule, List<TrackingInfo> events) {
        if (expectedMinutes == 0) return List.of();
        if (events == null || events.isEmpty()) {
            return List.of(new Incident("Unjustified absence", expectedMinutes, IncidentType.UNJUSTIFIED_ABSENCE));
        }
        List<Incident> incidents = new ArrayList<>();
        List<TrackingInfo> sorted = sortedEvents(events);
        incidents.addAll(checkEarlyExit(sorted, schedule));
        incidents.addAll(checkLateArrival(sorted, schedule));
        incidents.addAll(checkOvertime(expectedMinutes, actualMinutes));
        return incidents;

    }

    private static List<Incident> checkLateArrival(List<TrackingInfo> events, ScheduleInfo schedule) {
        List<Incident> incidents = new ArrayList<>();
        LocalTime expectedStart = schedule.intervals().getFirst().start();
        LocalTime deadline = expectedStart.plusMinutes(schedule.courtesyMinutes());
        sortedEvents(events).stream()
                .filter(p -> p.type().equals(TrackingType.In))
                .findFirst()
                .ifPresent(p -> {
                    LocalTime actualIn = LocalTime.ofInstant(p.ts(), ZoneId.systemDefault());
                    if (actualIn.isAfter(deadline)) {
                        int delay = (int) Duration.between(expectedStart, actualIn).toMinutes();
                        incidents.add(new Incident("Arrived " + delay + " minutes late", delay, IncidentType.LATE_ARRIVAL));
                    }
                });
        return incidents;
    }

    private static List<Incident> checkEarlyExit(List<TrackingInfo> events, ScheduleInfo schedule) {
        List<Incident> incidents = new ArrayList<>();
        LocalTime expectedEnd = schedule.intervals().getLast().end();
        LocalTime earliestExit = expectedEnd.minusMinutes(schedule.exitMarginMinutes());
        sortedEvents(events).stream()
                .filter(p -> p.type().equals(TrackingType.Out))
                .reduce((_, second) -> second)
                .ifPresent(p -> {
                    LocalTime actualOut = LocalTime.ofInstant(p.ts(), ZoneId.systemDefault());
                    if (actualOut.isBefore(earliestExit)) {
                        int early = (int) Duration.between(actualOut, expectedEnd).toMinutes();
                        incidents.add(new Incident("Left " + early + " minutes early", early, IncidentType.EARLY_EXIT));
                    }
                });
        return incidents;
    }
    private static List<Incident> checkOvertime(int expectedMinutes, int actualMinutes) {
        if (actualMinutes <= expectedMinutes) return List.of();
        int overtime = actualMinutes - expectedMinutes;
        return List.of(new Incident("Worked " + overtime + " minutes overtime", overtime, IncidentType.OVERTIME));
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
