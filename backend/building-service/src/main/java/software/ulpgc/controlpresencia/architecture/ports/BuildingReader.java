package software.ulpgc.controlpresencia.architecture.ports;

import software.ulpgc.controlpresencia.architecture.domain.Building;

import java.util.List;
import java.util.Optional;

public interface BuildingReader {
    List<Building> getAll();
    Optional<Building> get(String id);
}
