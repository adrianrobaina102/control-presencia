package software.ulpgc.controlpresencia.architecture.model;

import java.time.DayOfWeek;
import java.util.List;

public record ScheduleInfo(String id, String name, int courtesyMinutes, int exitMarginMinutes, List<TimeInterval> intervals, List<DayOfWeek> days) {
    public boolean appliesOn(DayOfWeek day) {
        return days != null && days.contains(day);
    }

    public int totalExpectedMinutes() {
        if (intervals == null) return 0;
        return intervals.stream()
                .mapToInt(TimeInterval::durationMinutes)
                .sum();
    }
}
