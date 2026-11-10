package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.util.Optional;

public class GetBuildingUseCase implements UseCase<String, Optional<Building>>  {
    private final BuildingReader reader;

    public GetBuildingUseCase(BuildingReader reader) {
        this.reader = reader;
    }

    @Override
    public Optional<Building> execute(String id) {
        return this.reader.get(id);
    }
}
