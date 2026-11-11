package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.domain.GetScheduleUseCase;

public class GetScheduleEndPoint implements EndPoint {
    private final GetScheduleUseCase useCase;
    private final Context context;
    private final Gson gson;

    public GetScheduleEndPoint(GetScheduleUseCase useCase, Context context, Gson gson) {
        this.useCase = useCase;
        this.context = context;
        this.gson = gson;
    }

    @Override
    public void execute() {
        String id = this.context.queryParam("id");
        if (id == null || id.isBlank()) {
            context.status(400).result("Missing id query parameter");
            return;
        }
        useCase.execute(id).ifPresentOrElse(
                schedule -> context.contentType("application/json").result(gson.toJson(schedule)),
                () -> context.status(404).result("Schedule not found")
        );
    }
}
