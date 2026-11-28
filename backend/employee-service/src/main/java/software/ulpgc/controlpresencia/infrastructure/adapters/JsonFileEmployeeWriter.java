package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonFileEmployeeWriter implements EmployeeWriter {
    private final File file;
    private final Gson gson;

    public JsonFileEmployeeWriter(File file, Gson gson) {
        this.file = file;
        this.gson = gson;
    }

    @Override
    public void write(List<Employee> employees) {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(employees, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
