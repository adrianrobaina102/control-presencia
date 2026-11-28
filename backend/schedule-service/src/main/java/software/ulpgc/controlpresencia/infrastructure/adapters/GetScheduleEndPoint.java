package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.usecases.GetScheduleUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

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
        Output<Schedule> output = useCase.execute(id);
        if (output.isSuccess())
        {
            ctx.contentType("application/json").result(gson.toJson(Output.ok(output)));
        }
        else
        {
            ctx.status(output.status()).result(output.error());
        }
    }
}
