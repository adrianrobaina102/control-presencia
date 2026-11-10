package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.domain.GetBuildingUseCase;

public class GetBuildingEndPoint implements EndPoint{
    private final GetBuildingUseCase useCase;
    private final Context context;
    private final Gson gson;
    public GetBuildingEndPoint(GetBuildingUseCase useCase, Context context, Gson gson) {
        this.useCase = useCase;
        this.context = context;
        this.gson = gson;
    }

    @Override
    public void execute() {
        String id = context.queryParam("id");
        if (id == null || id.isBlank()) {
            context.status(400).result("Missing id query parameter");
            return;
        }
        useCase.execute(id).ifPresentOrElse(
                building -> context.contentType("application/json").result(gson.toJson(building)),
                () -> context.status(404).result("Building not found")
        );
    }
}
