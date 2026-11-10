package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.GetBuildingUseCase;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.model.Organization;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBuildingUseCaseTest {

    @Test
    public void should_return_building_when_building_exists() {
        Building sampleBuilding = createSampleBuilding("bld-01", "Sede Central");
        BuildingReader mockReader = new BuildingReader() {
            @Override
            public Optional<Building> get(String id) {
                return "bld-01".equals(id) ? Optional.of(sampleBuilding) : Optional.empty();
            }

            @Override
            public List<Building> getAll() {
                return List.of(sampleBuilding);
            }
        };

        GetBuildingUseCase useCase = new GetBuildingUseCase(mockReader);
        Optional<Building> result = useCase.execute("bld-01");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Sede Central");
        assertThat(result.get().organization().name()).isEqualTo("Servicios Globales Canarios S.L.");
        assertThat(result.get().active()).isTrue();
    }

    @Test
    public void should_return_empty_optional_when_building_does_not_exist() {
        BuildingReader emptyReader = new BuildingReader() {
            @Override
            public Optional<Building> get(String id) {
                return Optional.empty();
            }

            @Override
            public List<Building> getAll() {
                return List.of();
            }
        };

        GetBuildingUseCase useCase = new GetBuildingUseCase(emptyReader);
        Optional<Building> result = useCase.execute("non-existent");

        assertThat(result).isEmpty();
    }

    private Building createSampleBuilding(String id, String name) {
        Organization org = new Organization("org-01", "Servicios Globales Canarios S.L.", "B-35123456");
        return new Building(id, name, "Calle Mayor de Triana 12", org, true);
    }
}
