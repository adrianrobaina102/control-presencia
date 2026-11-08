package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.domain.GetEmployeeUseCase;

public class GetEmployeeEndPoint implements EndPoint {
    private final GetEmployeeUseCase useCase;
    private final Context context;
    private final Gson gson;

    public GetEmployeeEndPoint(GetEmployeeUseCase useCase, Context context, Gson gson) {
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
                employee -> context.contentType("application/json").result(gson.toJson(employee)),
                () -> context.status(404).result("Employee not found")
        );
    }
}
