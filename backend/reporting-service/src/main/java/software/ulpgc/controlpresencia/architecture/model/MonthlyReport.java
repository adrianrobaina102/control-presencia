package software.ulpgc.controlpresencia.architecture.model;

import java.util.List;

public record MonthlyReport(String employeeId, String yearMonth, double totalExpectedHours, double totalActualHours, int totalDelayMinutes, int daysAbsent, List<DailyReport> reportList) {
    public static Builder builder()
    {
        return new Builder();
    }
    public static class Builder
    {
        private  String employeeId;
        private String yearMonth;
        private double totalExpectedHours;
        private double totalActualHours;
        private int totalDelayMinutes;
        private int daysAbsent;
        private List<DailyReport> reportList;
        public MonthlyReport.Builder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }
        public MonthlyReport.Builder yearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
            return this;
        }
        public MonthlyReport.Builder totalExpectedHours(double totalExpectedHours) {
            this.totalExpectedHours = totalExpectedHours;
            return this;
        }
        public MonthlyReport.Builder totalActualHours(double totalActualHours) {
            this.totalActualHours = totalActualHours;
            return this;
        }
        public MonthlyReport.Builder totalDelayMinutes(int totalDelayMinutes) {
            this.totalDelayMinutes = totalDelayMinutes;
            return this;
        }
        public MonthlyReport.Builder daysAbsent(int daysAbsent) {
            this.daysAbsent = daysAbsent;
            return this;
        }
        public MonthlyReport.Builder reportList(List<DailyReport> reportList) {
            this.reportList = reportList;
            return this;
        }
        public MonthlyReport build() {
            return new MonthlyReport(employeeId, yearMonth, totalExpectedHours, totalActualHours, totalDelayMinutes, daysAbsent, reportList);
        }
    }
}
