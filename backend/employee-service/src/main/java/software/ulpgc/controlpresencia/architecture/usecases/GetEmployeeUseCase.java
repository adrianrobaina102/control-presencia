package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.util.Optional;

public class GetEmployeeUseCase implements UseCase<String, Output<Employee>> {
    private final EmployeeReader reader;

    public GetEmployeeUseCase(EmployeeReader reader) {
        this.reader = reader;
    }

    @Override
    public Output<Employee> execute(String employeeId) {
        Optional<Employee> employee = this.reader.get(employeeId);
        return  employee.map(Output::ok).orElseGet(() -> Output.notFound("Employee not found"));
    }
}
