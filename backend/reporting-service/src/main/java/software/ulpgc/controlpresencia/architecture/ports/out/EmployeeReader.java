package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.EmployeeInfo;

import java.util.Optional;

public interface EmployeeReader {
    Optional<EmployeeInfo> get(String id);
}
