package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.DailyReport;
import software.ulpgc.controlpresencia.architecture.model.Incident;
import software.ulpgc.controlpresencia.architecture.model.IncidentType;
import software.ulpgc.controlpresencia.architecture.model.MonthlyReport;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenerateMonthlyReportUseCase implements UseCase<GenerateMonthlyReportUseCase.Param, Optional<MonthlyReport>> {
    private final GenerateDailyReportUseCase dailyUseCase;

    public GenerateMonthlyReportUseCase(GenerateDailyReportUseCase dailyUseCase) {
        this.dailyUseCase = dailyUseCase;
    }

    public record Param(String employeeId, YearMonth month) {}

    @Override
    public Optional<MonthlyReport> execute(Param input) {
        List<DailyReport> dailyReports = new ArrayList<>();
        for (int i = 1; i <= input.month().lengthOfMonth(); i++) {
            Optional<DailyReport> daily = dailyUseCase.execute(new GenerateDailyReportUseCase.Param(input.employeeId(), input.month().atDay(i)));
            if (daily.isEmpty()) return Optional.empty();
            dailyReports.add(daily.get());
        }
        return Optional.of(buildMonthlyReport(input, dailyReports));
    }

    private MonthlyReport buildMonthlyReport(Param input, List<DailyReport> dailyReports) {
        return MonthlyReport.builder()
                .employeeId(input.employeeId())
                .yearMonth(input.month().toString())
                .totalExpectedHours(sumExpectedHours(dailyReports))
                .totalActualHours(sumActualHours(dailyReports))
                .totalDelayMinutes(sumDelayMinutes(dailyReports))
                .daysAbsent(countAbsentDays(dailyReports))
                .reportList(dailyReports)
                .build();
    }

    private double sumExpectedHours(List<DailyReport> reports) {
        return reports.stream().mapToInt(DailyReport::expectedMinutes).sum() / 60.0;
    }

    private double sumActualHours(List<DailyReport> reports) {
        return reports.stream().mapToInt(DailyReport::actualMinutes).sum() / 60.0;
    }

    private int sumDelayMinutes(List<DailyReport> reports) {
        return reports.stream()
                .flatMap(report -> report.incidents().stream())
                .filter(incident -> incident.type() == IncidentType.LATE_ARRIVAL)
                .mapToInt(Incident::minutes)
                .sum();
    }

    private int countAbsentDays(List<DailyReport> reports) {
        return (int) reports.stream()
                .filter(report -> report.incidents().stream()
                        .anyMatch(incident -> incident.type() == IncidentType.UNJUSTIFIED_ABSENCE))
                .count();
    }
}
