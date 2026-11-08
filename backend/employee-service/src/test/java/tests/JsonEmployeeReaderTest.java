package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.infrastructure.adapters.JsonEmployeeReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonEmployeeReaderTest {

    @TempDir
    Path tempDir;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, ctx) -> new JsonPrimitive(date.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .create();

    @Test
    public void should_read_employees_from_json_file() throws IOException {
        String jsonContent = """
                [
                  {
                    "id": "emp-01",
                    "name": "Adrián Robaina",
                    "contact": { "email": "adrian@empresa.com", "phone": "+34 600 111 222" },
                    "role": "EMPLOYEE",
                    "contract": { "id": "CTR-01", "scheduleId": "SCH-OFFICE", "startDate": "2026-01-01", "status": "ACTIVE" }
                  }
                ]
                """;
        Path jsonFile = tempDir.resolve("employees.json");
        Files.writeString(jsonFile, jsonContent);

        JsonEmployeeReader reader = new JsonEmployeeReader(jsonFile.toFile(), gson);
        List<Employee> employees = reader.getAll();

        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).id()).isEqualTo("emp-01");
        assertThat(employees.get(0).name()).isEqualTo("Adrián Robaina");
    }

    @Test
    public void should_find_employee_by_id() throws IOException {
        String jsonContent = """
                [
                  {
                    "id": "emp-01",
                    "name": "Adrián Robaina",
                    "contact": { "email": "adrian@empresa.com", "phone": "+34 600 111 222" },
                    "role": "EMPLOYEE",
                    "contract": { "id": "CTR-01", "scheduleId": "SCH-OFFICE", "startDate": "2026-01-01", "status": "ACTIVE" }
                  },
                  {
                    "id": "emp-02",
                    "name": "María Comercio",
                    "contact": { "email": "maria@empresa.com", "phone": "+34 600 333 444" },
                    "role": "EMPLOYEE",
                    "contract": { "id": "CTR-02", "scheduleId": "SCH-STORE", "startDate": "2026-02-01", "status": "ACTIVE" }
                  }
                ]
                """;
        Path jsonFile = tempDir.resolve("employees.json");
        Files.writeString(jsonFile, jsonContent);

        JsonEmployeeReader reader = new JsonEmployeeReader(jsonFile.toFile(), gson);

        Optional<Employee> employee = reader.get("emp-02");
        assertThat(employee).isPresent();
        assertThat(employee.get().name()).isEqualTo("María Comercio");

        Optional<Employee> missing = reader.get("emp-99");
        assertThat(missing).isEmpty();
    }
}
