package software.ulpgc.controlpresencia.architecture.usecases;

import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.util.List;

public class ListBuildingsUseCase implements UseCase<Void, Output<List<Building>>> {
    private final BuildingReader reader;

    public ListBuildingsUseCase(BuildingReader reader) {
        this.reader = reader;
    }

    @Override
    public Output<List<Building>> execute(Void input) {
        return Output.ok(this.reader.getAll());
    }
}
