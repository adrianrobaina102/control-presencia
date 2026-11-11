package software.ulpgc.controlpresencia.architecture.model;

import java.util.List;

public record Schedule (String id, String name, int courtesyMinutes, int exitMarginMinutes, List<TimeInterval> intervals , ShiftType type, List<DayOfWeek> days ) {
    public boolean appliesOn(DayOfWeek day){
        return days != null && days.contains(day);
    }
    public int totalExpectedMinutes(){
        if (intervals == null) return 0;
        return intervals.stream()
                .mapToInt(TimeInterval::durationMinutes)
                .sum();
    }
}
