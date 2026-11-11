package software.ulpgc.controlpresencia.infrastructure.adapters;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.domain.ListSchedulesUseCase;

public class ListSchedulesEndPoint implements EndPoint {
    private final Gson gson;
    private final ListSchedulesUseCase useCase;
    private final Context context;

    public ListSchedulesEndPoint(Gson gson, ListSchedulesUseCase useCase, Context context) {
        this.gson = gson;
        this.useCase = useCase;
        this.context = context;
    }

    public ListSchedulesEndPoint(ListSchedulesUseCase useCase, Context context, Gson gson) {
        this(gson, useCase, context);
    }

    @Override
    public void execute() {
        context.contentType("application/json").result(gson.toJson(this.useCase.execute()));
    }
}
