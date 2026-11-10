package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.domain.ListBuildingsUseCase;

public class ListBuildingsEndPoint implements EndPoint {
    private final ListBuildingsUseCase useCase;
    private final Context context;
    private final Gson gson;

    public ListBuildingsEndPoint(ListBuildingsUseCase useCase, Context context, Gson gson) {
        this.useCase = useCase;
        this.context = context;
        this.gson = gson;
    }

    @Override
    public void execute() {
        this.context.contentType("application/json").result(gson.toJson(useCase.execute()));
    }
}
