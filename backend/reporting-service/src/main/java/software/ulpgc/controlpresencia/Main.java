package software.ulpgc.controlpresencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import io.javalin.Javalin;
import software.ulpgc.controlpresencia.architecture.ports.out.EmployeeReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventReader;
import software.ulpgc.controlpresencia.architecture.ports.out.ScheduleReader;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateDailyReportUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateMonthlyReportUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.GetDailyReportEndPoint;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.GetMonthlyReportEndPoint;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.HttpEmployeeReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.HttpEventReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.HttpScheduleReader;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();
        Gson gson = createGson();

        EmployeeReader employeeReader = new HttpEmployeeReader(httpClient, "http://localhost:7072", gson);
        ScheduleReader scheduleReader = new HttpScheduleReader(httpClient, "http://localhost:7074", gson);
        EventReader eventReader = new HttpEventReader(httpClient, "http://localhost:7071");

        GenerateDailyReportUseCase dailyUseCase = new GenerateDailyReportUseCase(employeeReader, eventReader, scheduleReader);
        GenerateMonthlyReportUseCase monthlyUseCase = new GenerateMonthlyReportUseCase(dailyUseCase);

        Javalin app = Javalin.create();
        new GetDailyReportEndPoint(gson, dailyUseCase).register(app);
        new GetMonthlyReportEndPoint(gson, monthlyUseCase).register(app);
        app.start(7075);

        System.out.println("Reporting-service activo en http://localhost:7075");
        System.out.println("-> Reporte diario:  http://localhost:7075/reports/daily?employeeId=emp-01&date=2026-11-02");
        System.out.println("-> Reporte mensual: http://localhost:7075/reports/monthly?employeeId=emp-01&month=2026-11");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, _, _) -> new JsonPrimitive(date.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, _, _) -> LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (time, _, _) -> new JsonPrimitive(time.toString()))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, _, _) -> LocalTime.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();
    }
}
