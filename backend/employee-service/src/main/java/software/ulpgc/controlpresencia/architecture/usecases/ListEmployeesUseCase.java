package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.util.List;

public class ListEmployeesUseCase implements UseCase<Void, Output<List<Employee>>> {
    private final EmployeeReader reader;

    public ListEmployeesUseCase(EmployeeReader reader) {
        this.reader = reader;
    }

    @Override
    public Output<List<Employee>> execute(Void input) {
        return Output.ok(this.reader.getAll());
    }
}
