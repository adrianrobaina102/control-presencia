package software.ulpgc.controlpresencia.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateDailyReportUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.EndPoint;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class GetDailyReportEndPoint implements EndPoint {
    private final Gson gson;
    private final GenerateDailyReportUseCase useCase;

    public GetDailyReportEndPoint(Gson gson, GenerateDailyReportUseCase useCase) {
        this.gson = gson;
        this.useCase = useCase;
    }

    @Override
    public void register(Javalin app) {
        app.get("/reports/daily", this::handle);
    }

    private void handle(Context ctx) {
        String employeeId = ctx.queryParam("employeeId");
        String dateParam = ctx.queryParam("date");
        if (employeeId == null || employeeId.isBlank() || dateParam == null || dateParam.isBlank()) {
            ctx.status(400).result("Missing employeeId or date query parameter");
            return;
        }

        LocalDate date = parseDate(dateParam);
        if (date == null) {
            ctx.status(400).result("Invalid date format. Expected YYYY-MM-DD");
            return;
        }

        ctx.future(() -> this.useCase.execute(new GenerateDailyReportUseCase.Param(employeeId, date))
                .thenAccept(output -> {
                    ctx.status(output.status());
                    if (output.error() == null) {
                        ctx.contentType("application/json").result(gson.toJson(output.result()));
                    } else {
                        ctx.result(output.error());
                    }
                }));
    }

    private LocalDate parseDate(String dateParam) {
        try {
            return LocalDate.parse(dateParam);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
