package software.ulpgc.controlpresencia;

import com.google.gson.*;
import io.javalin.Javalin;
import software.ulpgc.controlpresencia.architecture.domain.GetEmployeeUseCase;
import software.ulpgc.controlpresencia.architecture.domain.ListEmployeesUseCase;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.GetEmployeeEndPoint;
import software.ulpgc.controlpresencia.infrastructure.adapters.JsonEmployeeReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.ListEmployeesEndPoint;

import java.io.File;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        File file = resolveEmployeesFile();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, ctx) -> new JsonPrimitive(date.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();

        EmployeeReader reader = new JsonEmployeeReader(file, gson);
        GetEmployeeUseCase getEmployeeUseCase = new GetEmployeeUseCase(reader);
        ListEmployeesUseCase listEmployeesUseCase = new ListEmployeesUseCase(reader);

        Javalin app = Javalin.create().start(7072);

        app.get("/employees", context -> {
            if (context.queryParam("id") != null) {
                new GetEmployeeEndPoint(getEmployeeUseCase, context, gson).execute();
            } else {
                new ListEmployeesEndPoint(listEmployeesUseCase, context, gson).execute();
            }
        });

        System.out.println("Employee-service activo en http://localhost:7072");
        System.out.println("-> Listar empleados: http://localhost:7072/employees");
        System.out.println("-> Consultar empleado: http://localhost:7072/employees?id=emp-01");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static File resolveEmployeesFile() {
        File file = new File("backend/employee-service/employees.json");
        if (file.exists()) return file;

        file = new File("employee-service/employees.json");
        if (file.exists()) return file;

        return new File("employees.json");
    }
}
