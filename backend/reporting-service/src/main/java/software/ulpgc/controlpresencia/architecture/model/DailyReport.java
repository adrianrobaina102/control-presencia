package software.ulpgc.controlpresencia.architecture.domain;

import java.time.LocalDate;
import java.util.List;

public record DailyReport (String employeeId, LocalDate date, int actualMinutes, int expectedMinutes, int balanceMinutes, List<Incident> incidents) {
    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder{
        private String employeeId;
        private LocalDate date;
        private int actualMinutes;
        private int expectedMinutes;
        private int balanceMinutes;
        private List<Incident> incidents;

        public Builder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }
        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }
        public Builder actualMinutes(int actualMinutes) {
            this.actualMinutes = actualMinutes;
            return this;
        }
        public Builder expectedMinutes(int expectedMinutes) {
            this.expectedMinutes = expectedMinutes;
            return this;
        }
        public Builder balanceMinutes(int balanceMinutes) {
            this.balanceMinutes = balanceMinutes;
            return this;
        }
        public Builder incidents(List<Incident> incidents) {
            this.incidents = incidents;
            return this;
        }
        public DailyReport build() {
            return new DailyReport(employeeId, date, actualMinutes, expectedMinutes, balanceMinutes, incidents);
        }
    }
}
