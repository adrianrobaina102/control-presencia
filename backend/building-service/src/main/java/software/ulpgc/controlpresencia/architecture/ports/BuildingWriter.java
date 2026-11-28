package software.ulpgc.controlpresencia.architecture.ports;

import software.ulpgc.controlpresencia.architecture.model.Building;

import java.util.List;

public interface BuildingWriter {
    void write(List<Building> buildings);
}
