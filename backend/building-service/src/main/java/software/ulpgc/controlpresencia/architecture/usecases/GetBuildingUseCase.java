package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.util.Optional;

public class GetBuildingUseCase implements UseCase<String, Output<Building>>  {
    private final BuildingReader reader;

    public GetBuildingUseCase(BuildingReader reader) {
        this.reader = reader;
    }

    @Override
    public Output<Building> execute(String id) {
        Optional<Building> building = this.reader.get(id);
        return building.map(Output::ok).orElseGet(() -> Output.notFound("Building not found"));
    }
}
