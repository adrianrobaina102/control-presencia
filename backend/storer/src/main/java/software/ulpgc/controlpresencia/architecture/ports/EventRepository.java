package software.ulpgc.controlpresencia.architecture.ports;

public interface EventRepository {
    void save(String eventText);
}
