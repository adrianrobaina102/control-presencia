package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.model.Organization;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;
import software.ulpgc.controlpresencia.architecture.usecases.GetBuildingUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

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
        Output<Building> output = useCase.execute("bld-01");

        assertThat(output.isSuccess()).isTrue();
        assertThat(output.status()).isEqualTo(200);
        assertThat(output.result().name()).isEqualTo("Sede Central");
        assertThat(output.result().organization().name()).isEqualTo("Servicios Globales Canarios S.L.");
        assertThat(output.result().active()).isTrue();
    }

    @Test
    public void should_return_not_found_when_building_does_not_exist() {
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
        Output<Building> output = useCase.execute("non-existent");

        assertThat(output.isSuccess()).isFalse();
        assertThat(output.status()).isEqualTo(404);
        assertThat(output.error()).isEqualTo("Building not found");
    }

    private Building createSampleBuilding(String id, String name) {
        Organization org = new Organization("org-01", "Servicios Globales Canarios S.L.", "B-35123456");
        return new Building(id, name, "Calle Mayor de Triana 12", org, true);
    }
}
