package software.ulpgc.controlpresencia.architecture.ports;

import software.ulpgc.controlpresencia.architecture.model.Employee;

import java.util.List;

public interface EmployeeWriter {
    void write(List<Employee> employees);
}
