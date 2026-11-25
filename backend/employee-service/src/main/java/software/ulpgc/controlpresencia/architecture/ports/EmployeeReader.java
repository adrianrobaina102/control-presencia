package software.ulpgc.controlpresencia.architecture.ports;

import software.ulpgc.controlpresencia.architecture.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeReader {
    Optional<Employee> get(String id);
    List<Employee> getAll();
}
