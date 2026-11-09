package software.ulpgc.controlpresencia.architecture.domain;

public interface UseCase<I, O> {
    O execute(I input);
    default O execute() {
        return this.execute(null);
    }
}
