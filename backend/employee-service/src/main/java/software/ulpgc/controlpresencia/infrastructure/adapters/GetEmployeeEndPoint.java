package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.usecases.GetEmployeeUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

public class GetEmployeeEndPoint implements EndPoint {
    private final GetEmployeeUseCase useCase;
    private final Gson gson;

    public GetEmployeeEndPoint(GetEmployeeUseCase useCase, Gson gson) {
        this.useCase = useCase;
        this.gson = gson;
    }

    @Override
    public void execute(Javalin app) {
        app.get("/employees/{id}", this::handle);
    }

    private void handle(Context ctx) {
        String id = ctx.pathParam("id");
        if (id.isBlank()) {
            ctx.status(400).result("Missing id path parameter");
            return;
        }
        Output<Employee> output = useCase.execute(id);
        if (output.isSuccess())
        {
            ctx.contentType("application/json").result(gson.toJson(output.result()));
        }
        else {
            ctx.status(output.status()).result(output.error());
        }
    }
}
