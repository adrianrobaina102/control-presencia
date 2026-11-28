package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.model.Building;
import software.ulpgc.controlpresencia.architecture.usecases.GetBuildingUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

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

        Output<Building> output = useCase.execute(id);
        if (output.isSuccess()) {
            ctx.status(output.status()).contentType("application/json").result(gson.toJson(output.result()));
        } else {
            ctx.status(output.status()).result(output.error());
        }
    }
}
