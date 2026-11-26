package software.ulpgc.controlpresencia.architecture.ports.out;

import software.ulpgc.controlpresencia.architecture.model.EmployeeInfo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface EmployeeReader {
    CompletableFuture<Optional<EmployeeInfo>> get(String id);
}
