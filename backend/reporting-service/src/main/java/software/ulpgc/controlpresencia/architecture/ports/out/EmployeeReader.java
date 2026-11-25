package software.ulpgc.controlpresencia.architecture.ports.in;

import software.ulpgc.controlpresencia.architecture.model.Employee;

import java.util.Optional;

public interface EmployeeReader {
    Optional<Employee> get(String id);
}
