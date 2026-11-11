package software.ulpgc.controlpresencia;

import com.google.gson.*;
import io.javalin.Javalin;
import software.ulpgc.controlpresencia.architecture.domain.GetScheduleUseCase;
import software.ulpgc.controlpresencia.architecture.domain.ListSchedulesUseCase;
import software.ulpgc.controlpresencia.architecture.ports.ScheduleReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.GetScheduleEndPoint;
import software.ulpgc.controlpresencia.infrastructure.adapters.JsonScheduleReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.ListSchedulesEndPoint;

import java.io.File;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        File file = resolveSchedulesFile();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (time, type, ctx) -> new JsonPrimitive(time.toString()))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, ctx) -> LocalTime.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();

        ScheduleReader reader = new JsonScheduleReader(file, gson);
        GetScheduleUseCase getScheduleUseCase = new GetScheduleUseCase(reader);
        ListSchedulesUseCase listSchedulesUseCase = new ListSchedulesUseCase(reader);

        Javalin app = Javalin.create().start(7074);

        app.get("/schedules", context -> {
            if (context.queryParam("id") != null) {
                new GetScheduleEndPoint(getScheduleUseCase, context, gson).execute();
            } else {
                new ListSchedulesEndPoint(listSchedulesUseCase, context, gson).execute();
            }
        });

        System.out.println("Schedule-service activo en http://localhost:7074");
        System.out.println("-> Listar horarios: http://localhost:7074/schedules");
        System.out.println("-> Consultar horario: http://localhost:7074/schedules?id=SCH-OFFICE-CONTINUOUS");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static File resolveSchedulesFile() {
        File file = new File("backend/schedule-service/schedules.json");
        if (file.exists()) return file;

        file = new File("schedule-service/schedules.json");
        if (file.exists()) return file;

        return new File("schedules.json");
    }
}
