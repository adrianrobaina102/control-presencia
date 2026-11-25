package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.MonthlyReport;

import java.time.YearMonth;
import java.util.Optional;

public class GenerateMonthlyReportUseCase implements UseCase <GenerateMonthlyReportUseCase.Param, Optional<MonthlyReport>>{

    public record Param(String employeeId, YearMonth month) {}
    @Override
    public Optional<MonthlyReport> execute(Param input) {
        return Optional.ofNullable(MonthlyReport.builder().employeeId(input.employeeId).yearMonth(String.valueOf(input.month)).build());
    }
}
