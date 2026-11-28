package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.domain.DailyReportCalculator;
import software.ulpgc.controlpresencia.architecture.model.DailyReport;
import software.ulpgc.controlpresencia.architecture.model.EmployeeInfo;
import software.ulpgc.controlpresencia.architecture.model.ScheduleInfo;
import software.ulpgc.controlpresencia.architecture.model.TrackingInfo;
import software.ulpgc.controlpresencia.architecture.ports.out.EmployeeReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventReader;
import software.ulpgc.controlpresencia.architecture.ports.out.ScheduleReader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GenerateDailyReportUseCase implements UseCase<GenerateDailyReportUseCase.Param, CompletableFuture<Output<DailyReport>>> {

    public record Param(String employeeId, LocalDate date) {}

    private final EmployeeReader employeeReader;
    private final EventReader eventReader;
    private final ScheduleReader scheduleReader;

    public GenerateDailyReportUseCase(EmployeeReader employeeReader, EventReader eventReader, ScheduleReader scheduleReader) {
        this.employeeReader = employeeReader;
        this.eventReader = eventReader;
        this.scheduleReader = scheduleReader;
    }

    @Override
    public CompletableFuture<Output<DailyReport>> execute(Param input) {
        return employeeReader.get(input.employeeId())
                .thenCompose(employee -> employee
                        .map(emp -> fetchScheduleAndEvents(emp, input.date()))
                        .orElseGet(() -> CompletableFuture.completedFuture(Output.notFound("Employee not found"))));
    }

    private CompletableFuture<Output<DailyReport>> fetchScheduleAndEvents(EmployeeInfo employee, LocalDate date) {
        CompletableFuture<Optional<ScheduleInfo>> scheduleFuture = scheduleReader.get(employee.scheduleId());
        CompletableFuture<List<TrackingInfo>> eventsFuture = eventReader.getEvents(employee.id(), date);
        return scheduleFuture.thenCombine(eventsFuture, (scheduleOpt, events) ->
                scheduleOpt.map(schedule -> Output.ok(DailyReportCalculator.calculate(employee.id(), date, schedule, events)))
                        .orElseGet(() -> Output.notFound("Schedule not found")));
    }
}
