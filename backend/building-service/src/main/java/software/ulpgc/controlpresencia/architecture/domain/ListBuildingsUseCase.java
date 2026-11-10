package software.ulpgc.controlpresencia.architecture.domain;

import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.util.List;

public class ListBuildingsUseCase implements UseCase<Void,List<Building> >  {
    private final BuildingReader reader;

    public ListBuildingsUseCase(BuildingReader reader) {
        this.reader = reader;
    }

    @Override
    public List<Building> execute(Void input) {
        return this.reader.getAll();
    }
}
