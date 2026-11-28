package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.usecases.GetScheduleUseCase;

public class GetScheduleEndPoint implements EndPoint {
    private final GetScheduleUseCase useCase;
    private final Gson gson;

    public GetScheduleEndPoint(GetScheduleUseCase useCase, Gson gson) {
        this.useCase = useCase;
        this.gson = gson;
    }

    @Override
    public void execute(Javalin app) {
        app.get("/schedules/{id}", this::handle);
    }

    private void handle(Context ctx) {
        String id = ctx.pathParam("id");
        if (id.isBlank()) {
            ctx.status(400).result("Missing id path parameter");
            return;
        }
        useCase.execute(id).ifPresentOrElse(
                schedule -> ctx.contentType("application/json").result(gson.toJson(schedule)),
                () -> ctx.status(404).result("Schedule not found")
        );
    }
}
