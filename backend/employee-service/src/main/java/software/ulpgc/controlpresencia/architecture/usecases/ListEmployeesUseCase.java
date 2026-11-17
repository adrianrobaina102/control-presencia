package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.domain.Employee;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.util.List;

public class ListEmployeesUseCase implements UseCase<Void, List<Employee>> {
    private final EmployeeReader reader;

    public ListEmployeesUseCase(EmployeeReader reader) {
        this.reader = reader;
    }

    @Override
    public List<Employee> execute(Void input) {
        return this.reader.getAll();
    }
}
