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

public class GenerateDailyReportUseCase implements UseCase<GenerateDailyReportUseCase.Param, Optional<DailyReport>> {

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
    public Optional<DailyReport> execute(Param input) {
        Optional<EmployeeInfo> employee = employeeReader.get(input.employeeId());
        if (employee.isEmpty()) return Optional.empty();
        Optional<ScheduleInfo> schedule = scheduleReader.get(employee.get().scheduleId());
        if (schedule.isEmpty()) return Optional.empty();

        List<TrackingInfo> events = eventReader.getEvents(input.employeeId(), input.date());
        return Optional.of(DailyReportCalculator.calculate(input.employeeId(), input.date(), schedule.get(), events));
    }
}
