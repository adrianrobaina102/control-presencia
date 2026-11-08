package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonEmployeeReader implements EmployeeReader {
    private final File file;
    private final Gson gson;
    public JsonEmployeeReader(File file, Gson gson) {
        this.file = file;
        this.gson = gson;
    }
    @Override
    public Optional<Employee> get(String id) {
        return getAll().stream()
                .filter(employee -> employee.id().equals(id))
                .findFirst();

    }

    @Override
    public List<Employee> getAll() {
        try(FileReader fileReader = new FileReader(file))
        {
            Employee[] employees = this.gson.fromJson(fileReader, Employee[].class);
            return employees == null ? List.of() : Arrays.asList(employees);
        } catch (IOException e) {
            return List.of();
        }
    }
}
