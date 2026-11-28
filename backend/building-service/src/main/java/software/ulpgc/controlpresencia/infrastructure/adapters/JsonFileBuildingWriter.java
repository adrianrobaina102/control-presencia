package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.ports.BuildingWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonFileBuildingWriter implements BuildingWriter {
    private final File file;
    private final Gson gson;

    public JsonFileBuildingWriter(File file, Gson gson) {
        this.file = file;
        this.gson = gson;
    }

    @Override
    public void write(List<Building> buildings) {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(buildings, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
