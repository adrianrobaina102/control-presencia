package software.ulpgc.controlpresencia.architecture.domain;

import java.util.List;

public record Schedule (String id, String name, int courtesyMinutes, int exitMarginMinutes, List<TimeInterval> intervals , ShiftType type, List<DayOfWeek> days ) {
    public static Builder builder() {
        return new Builder();
    }
    public boolean appliesOn(DayOfWeek day){
        return days != null && days.contains(day);
    }
    public int totalExpectedMinutes(){
        if (intervals == null) return 0;
        return intervals.stream()
                .mapToInt(TimeInterval::durationMinutes)
                .sum();
    }
    public static class Builder {
        private String id;
        private String name;
        private int courtesyMinutes;
        private int exitMarginMinutes;
        private List<TimeInterval> intervals;
        private ShiftType type;
        private List<DayOfWeek> days;

        public Schedule.Builder id(String id) {
            this.id = id;
            return this;
        }

        public Schedule.Builder name(String name) {
            this.name = name;
            return this;
        }

        public Schedule.Builder courtesyMinutes(int courtesyMinutes) {
            this.courtesyMinutes = courtesyMinutes;
            return this;
        }

        public Schedule.Builder exitMarginMinutes(int exitMarginMinutes) {
            this.exitMarginMinutes = exitMarginMinutes;
            return this;
        }

        public Schedule.Builder intervals(List<TimeInterval> intervals) {
            this.intervals = intervals;
            return this;
        }

        public Schedule.Builder type(ShiftType type) {
            this.type = type;
            return this;
        }

        public Schedule.Builder days(List<DayOfWeek> days) {
            this.days = days;
            return this;
        }
        public Schedule build() {
            return new Schedule(id, name, courtesyMinutes, exitMarginMinutes, intervals, type, days);
        }
    }

}
