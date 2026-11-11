package tests;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.infrastructure.adapters.JsonScheduleReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonScheduleReaderTest {

    @TempDir
    Path tempDir;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (time, type, ctx) -> new JsonPrimitive(time.toString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, ctx) -> LocalTime.parse(json.getAsString()))
            .create();

    @Test
    public void should_read_schedules_from_json_file() throws IOException {
        String jsonContent = """
                [
                  {
                    "id": "SCH-OFFICE-CONTINUOUS",
                    "name": "Jornada Continua - Oficina Tecnologica",
                    "type": "CONTINUOUS",
                    "courtesyMinutes": 10,
                    "exitMarginMinutes": 5,
                    "days": [
                      "MONDAY",
                      "TUESDAY",
                      "WEDNESDAY",
                      "THURSDAY",
                      "FRIDAY"
                    ],
                    "intervals": [
                      {
                        "start": "08:00:00",
                        "end": "16:00:00"
                      }
                    ]
                  }
                ]
                """;
        Path jsonFile = tempDir.resolve("schedules.json");
        Files.writeString(jsonFile, jsonContent);

        JsonScheduleReader reader = new JsonScheduleReader(jsonFile.toFile(), gson);
        List<Schedule> schedules = reader.getAll();

        assertThat(schedules).hasSize(1);
        Schedule sched = schedules.getFirst();
        assertThat(sched.id()).isEqualTo("SCH-OFFICE-CONTINUOUS");
        assertThat(sched.name()).isEqualTo("Jornada Continua - Oficina Tecnologica");
        assertThat(sched.type()).isEqualTo(ShiftType.CONTINUOUS);
        assertThat(sched.courtesyMinutes()).isEqualTo(10);
        assertThat(sched.intervals()).hasSize(1);
        assertThat(sched.intervals().getFirst().start()).isEqualTo(LocalTime.of(8, 0));
        assertThat(sched.intervals().getFirst().end()).isEqualTo(LocalTime.of(16, 0));
        assertThat(sched.totalExpectedMinutes()).isEqualTo(480);
    }

    @Test
    public void should_find_schedule_by_id() throws IOException {
        String jsonContent = """
                [
                  {
                    "id": "SCH-01",
                    "name": "Oficina",
                    "type": "CONTINUOUS",
                    "courtesyMinutes": 10,
                    "exitMarginMinutes": 5,
                    "days": ["MONDAY"],
                    "intervals": [{ "start": "08:00:00", "end": "16:00:00" }]
                  },
                  {
                    "id": "SCH-02",
                    "name": "Comercio",
                    "type": "SPLIT",
                    "courtesyMinutes": 10,
                    "exitMarginMinutes": 5,
                    "days": ["MONDAY", "SATURDAY"],
                    "intervals": [
                      { "start": "09:30:00", "end": "13:30:00" },
                      { "start": "16:30:00", "end": "20:30:00" }
                    ]
                  }
                ]
                """;
        Path jsonFile = tempDir.resolve("schedules.json");
        Files.writeString(jsonFile, jsonContent);

        JsonScheduleReader reader = new JsonScheduleReader(jsonFile.toFile(), gson);
        Optional<Schedule> sched = reader.get("SCH-02");

        assertThat(sched).isPresent();
        assertThat(sched.get().name()).isEqualTo("Comercio");
        assertThat(sched.get().type()).isEqualTo(ShiftType.SPLIT);
        assertThat(sched.get().intervals()).hasSize(2);
        assertThat(sched.get().totalExpectedMinutes()).isEqualTo(480); // 4h + 4h = 8h = 480 min
    }

    @Test
    public void should_return_empty_list_when_file_not_found() {
        Path nonExistent = tempDir.resolve("non-existent.json");
        JsonScheduleReader reader = new JsonScheduleReader(nonExistent.toFile(), gson);

        List<Schedule> schedules = reader.getAll();

        assertThat(schedules).isEmpty();
    }
}
