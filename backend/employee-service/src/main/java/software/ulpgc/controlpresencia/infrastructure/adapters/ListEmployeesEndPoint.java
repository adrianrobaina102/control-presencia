package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.domain.ListEmployeesUseCase;

public class ListEmployeesEndPoint implements EndPoint {
    private final ListEmployeesUseCase useCase;
    private final Context context;
    private final Gson gson;

    public ListEmployeesEndPoint(ListEmployeesUseCase useCase, Context context, Gson gson) {
        this.useCase = useCase;
        this.context = context;
        this.gson = gson;
    }

    @Override
    public void execute() {
        this.context.contentType("application/json").result(gson.toJson(useCase.execute()));
    }
}
