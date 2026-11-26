package software.ulpgc.controlpresencia.infrastructure.adapters.out;

import software.ulpgc.controlpresencia.architecture.model.EmployeeInfo;
import software.ulpgc.controlpresencia.architecture.ports.out.EmployeeReader;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HttpEmployeeReader implements EmployeeReader {
    @Override
    public CompletableFuture<Optional<EmployeeInfo>> get(String id) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
