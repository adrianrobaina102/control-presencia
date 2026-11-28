package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.usecases.ListBuildingsUseCase;

public class ListBuildingsEndPoint implements EndPoint {
    private final ListBuildingsUseCase useCase;
    private final Gson gson;

    public ListBuildingsEndPoint(ListBuildingsUseCase useCase, Gson gson) {
        this.useCase = useCase;
        this.gson = gson;
    }

    @Override
    public void execute(Javalin app) {
        app.get("/buildings", this::handle);
    }

    private void handle(Context ctx) {
        ctx.contentType("application/json").result(gson.toJson(useCase.execute()));
    }
}
