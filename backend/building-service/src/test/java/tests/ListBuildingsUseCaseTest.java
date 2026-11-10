package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.ListBuildingsUseCase;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.model.Organization;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ListBuildingsUseCaseTest {

    @Test
    public void should_return_all_buildings() {
        Building bld1 = createSampleBuilding("bld-01", "Sede Central");
        Building bld2 = createSampleBuilding("bld-02", "Tienda Triana");

        BuildingReader mockReader = new BuildingReader() {
            @Override
            public Optional<Building> get(String id) {
                return Optional.empty();
            }

            @Override
            public List<Building> getAll() {
                return List.of(bld1, bld2);
            }
        };

        ListBuildingsUseCase useCase = new ListBuildingsUseCase(mockReader);
        List<Building> result = useCase.execute();

        assertThat(result).hasSize(2)
                .extracting(Building::name)
                .containsExactly("Sede Central", "Tienda Triana");
    }

    private Building createSampleBuilding(String id, String name) {
        Organization org = new Organization("org-01", "Servicios Globales Canarios S.L.", "B-35123456");
        return new Building(id, name, "Calle Mayor de Triana 12", org, true);
    }
}
