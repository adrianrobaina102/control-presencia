package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.model.Schedule;
import software.ulpgc.controlpresencia.architecture.usecases.ListSchedulesUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

import java.util.List;

public class ListSchedulesEndPoint implements EndPoint {
    private final Gson gson;
    private final ListSchedulesUseCase useCase;

    public ListSchedulesEndPoint(Gson gson, ListSchedulesUseCase useCase) {
        this.gson = gson;
        this.useCase = useCase;
    }

    public ListSchedulesEndPoint(ListSchedulesUseCase useCase, Gson gson) {
        this(gson, useCase);
    }

    @Override
    public void execute(Javalin app) {
        app.get("/schedules", this::handle);
    }

    private void handle(Context ctx) {
        Output<List<Schedule>> output = this.useCase.execute();
        ctx.contentType("application/json").result(gson.toJson(output.result()));
    }
}
