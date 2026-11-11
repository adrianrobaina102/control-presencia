package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonScheduleReader implements ScheduleReader {
    private final File file;
    private final Gson gson;

    public JsonScheduleReader(File file, Gson gson) {
        this.file = file;
        this.gson = gson;
    }

    public JsonScheduleReader(Gson gson, File file) {
        this(file, gson);
    }

    @Override
    public Optional<Schedule> get(String id) {
        return getAll().stream().filter(p -> p.id().equals(id)).findFirst();
    }

    @Override
    public List<Schedule> getAll() {
        try (FileReader reader = new FileReader(file)) {
            Schedule[] schedules = this.gson.fromJson(reader, Schedule[].class);
            return schedules == null ? List.of() : Arrays.asList(schedules);
        } catch (IOException e) {
            return List.of();
        }
    }
}
