package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.DailyReport;
import software.ulpgc.controlpresencia.architecture.model.Incident;
import software.ulpgc.controlpresencia.architecture.model.IncidentType;
import software.ulpgc.controlpresencia.architecture.model.MonthlyReport;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class GenerateMonthlyReportUseCase implements UseCase<GenerateMonthlyReportUseCase.Param, CompletableFuture<Optional<MonthlyReport>>> {
    private final GenerateDailyReportUseCase dailyUseCase;

    public GenerateMonthlyReportUseCase(GenerateDailyReportUseCase dailyUseCase) {
        this.dailyUseCase = dailyUseCase;
    }

    public record Param(String employeeId, YearMonth month) {}

    @Override
    public CompletableFuture<Optional<MonthlyReport>> execute(Param input) {
        List<CompletableFuture<Optional<DailyReport>>> futures = IntStream.rangeClosed(1, input.month().lengthOfMonth())
                .mapToObj(day -> dailyUseCase.execute(new GenerateDailyReportUseCase.Param(input.employeeId(), input.month().atDay(day))))
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(_ -> combineDailyReports(input, futures));
    }

    private Optional<MonthlyReport> combineDailyReports(Param input, List<CompletableFuture<Optional<DailyReport>>> futures) {
        List<Optional<DailyReport>> results = futures.stream().map(CompletableFuture::join).toList();
        if (results.stream().anyMatch(Optional::isEmpty)) return Optional.empty();
        List<DailyReport> dailyReports = results.stream().map(Optional::get).toList();
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
