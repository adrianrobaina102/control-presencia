package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonBuildingReader implements BuildingReader {
    private final File file;
    private final Gson gson;

    public JsonBuildingReader(File file, Gson gson) {
        this.file = file;
        this.gson = gson;
    }

    public JsonBuildingReader(Gson gson, File file) {
        this(file, gson);
    }

    @Override
    public List<Building> getAll() {
        try (FileReader fileReader = new FileReader(file)) {
            Building[] buildings = this.gson.fromJson(fileReader, Building[].class);
            return buildings == null ? List.of() : Arrays.asList(buildings);
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public Optional<Building> get(String id) {
        return getAll().stream()
                .filter(building -> building.id().equals(id))
                .findFirst();
    }
}
