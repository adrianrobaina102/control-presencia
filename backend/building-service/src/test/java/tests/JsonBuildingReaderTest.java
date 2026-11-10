package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.infrastructure.adapters.JsonBuildingReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonBuildingReaderTest {

    @TempDir
    Path tempDir;

    private final Gson gson = new GsonBuilder().create();

    @Test
    public void should_read_buildings_from_json_file() throws IOException {
        String jsonContent = """
                [
                  {
                    "id": "bld-01",
                    "name": "Sede Central",
                    "address": "Calle Mayor de Triana 12",
                    "organization": {
                      "id": "org-01",
                      "name": "Servicios Globales Canarios S.L.",
                      "taxId": "B-35123456"
                    },
                    "active": true
                  }
                ]
                """;
        Path jsonFile = tempDir.resolve("buildings.json");
        Files.writeString(jsonFile, jsonContent);

        JsonBuildingReader reader = new JsonBuildingReader(jsonFile.toFile(), gson);
        List<Building> buildings = reader.getAll();

        assertThat(buildings).hasSize(1);
        Building bld = buildings.getFirst();
        assertThat(bld.id()).isEqualTo("bld-01");
        assertThat(bld.name()).isEqualTo("Sede Central");
        assertThat(bld.organization().taxId()).isEqualTo("B-35123456");
        assertThat(bld.active()).isTrue();
    }

    @Test
    public void should_find_building_by_id() throws IOException {
        String jsonContent = """
                [
                  {
                    "id": "bld-01",
                    "name": "Sede Central",
                    "address": "Calle Mayor 12",
                    "organization": {
                      "id": "org-01",
                      "name": "Org 1",
                      "taxId": "B-111"
                    },
                    "active": true
                  },
                  {
                    "id": "bld-02",
                    "name": "Tienda Triana",
                    "address": "Calle Triana 88",
                    "organization": {
                      "id": "org-01",
                      "name": "Org 1",
                      "taxId": "B-111"
                    },
                    "active": true
                  }
                ]
                """;
        Path jsonFile = tempDir.resolve("buildings.json");
        Files.writeString(jsonFile, jsonContent);

        JsonBuildingReader reader = new JsonBuildingReader(jsonFile.toFile(), gson);
        Optional<Building> bld = reader.get("bld-02");

        assertThat(bld).isPresent();
        assertThat(bld.get().name()).isEqualTo("Tienda Triana");
    }

    @Test
    public void should_return_empty_list_when_file_not_found() {
        Path nonExistent = tempDir.resolve("non-existent.json");
        JsonBuildingReader reader = new JsonBuildingReader(nonExistent.toFile(), gson);

        List<Building> buildings = reader.getAll();

        assertThat(buildings).isEmpty();
    }
}
