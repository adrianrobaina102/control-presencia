package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.usecases.GetBuildingUseCase;

public class GetBuildingEndPoint implements EndPoint{
    private final GetBuildingUseCase useCase;
    private final Gson gson;
    public GetBuildingEndPoint(GetBuildingUseCase useCase, Gson gson) {
        this.useCase = useCase;
        this.gson = gson;
    }

    @Override
    public void execute(Javalin app) {
        app.get("/buildings/{id}", this::handle);
    }

    private void handle(Context ctx) {
        String id = ctx.pathParam("id");
        if (id.isBlank()) {
            ctx.status(400).result("Missing id path parameter");
            return;
        }
        useCase.execute(id).ifPresentOrElse(
                building -> ctx.contentType("application/json").result(gson.toJson(building)),
                () -> ctx.status(404).result("Building not found")
        );
    }
}
