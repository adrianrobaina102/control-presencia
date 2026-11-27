package software.ulpgc.controlpresencia.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.usecases.GenerateMonthlyReportUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.EndPoint;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class GetMonthlyReportEndPoint implements EndPoint {
    private final Gson gson;
    private final GenerateMonthlyReportUseCase useCase;

    public GetMonthlyReportEndPoint(Gson gson, GenerateMonthlyReportUseCase useCase) {
        this.gson = gson;
        this.useCase = useCase;
    }

    @Override
    public void register(Javalin app) {
        app.get("/reports/monthly", this::handle);
    }

    private void handle(Context ctx) {
        String employeeId = ctx.queryParam("employeeId");
        String monthParam = ctx.queryParam("month");
        if (employeeId == null || employeeId.isBlank() || monthParam == null || monthParam.isBlank()) {
            ctx.status(400).result("Missing employeeId or month query parameter");
            return;
        }

        YearMonth month = parseMonth(monthParam);
        if (month == null) {
            ctx.status(400).result("Invalid month format. Expected YYYY-MM");
            return;
        }

        ctx.future(() -> this.useCase.execute(new GenerateMonthlyReportUseCase.Param(employeeId, month))
                .thenAccept(reportOpt -> {
                    if (reportOpt.isPresent()) {
                        ctx.status(200).contentType("application/json").result(gson.toJson(reportOpt.get()));
                    } else {
                        ctx.status(404).result("Monthly report not found");
                    }
                }));
    }

    private YearMonth parseMonth(String monthParam) {
        try {
            return YearMonth.parse(monthParam);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
