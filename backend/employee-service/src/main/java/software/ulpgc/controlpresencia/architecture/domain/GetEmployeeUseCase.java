package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.util.Optional;

public class GetEmployeeUseCase implements UseCase<String, Optional<Employee>> {
    private final EmployeeReader reader;

    public GetEmployeeUseCase(EmployeeReader reader) {
        this.reader = reader;
    }

    @Override
    public Optional<Employee> execute(String employeeId) {
        return this.reader.get(employeeId);
    }
}
