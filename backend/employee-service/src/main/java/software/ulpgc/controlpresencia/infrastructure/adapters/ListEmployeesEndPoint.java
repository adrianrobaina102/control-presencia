package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.usecases.ListEmployeesUseCase;
import software.ulpgc.controlpresencia.architecture.usecases.Output;

import java.util.List;

public class ListEmployeesEndPoint implements EndPoint {
    private final ListEmployeesUseCase useCase;
    private final Gson gson;

    public ListEmployeesEndPoint(ListEmployeesUseCase useCase, Gson gson) {
        this.useCase = useCase;
        this.gson = gson;
    }

    @Override
    public void execute(Javalin app) {
        app.get("/employees", this::handle);
    }

    private void handle(Context ctx) {
        Output<List<Employee>> output = useCase.execute();
        ctx.contentType("application/json").result(gson.toJson(output.result()));
    }
}
