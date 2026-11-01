package software.ulpgc.controlpresencia.infrastructure.adapters.endpoints;

import io.javalin.http.Context;
import software.ulpgc.controlpresencia.application.usecases.GetEventsUseCase;

public class GetEventsEndPoint implements EndPoint{
    private final GetEventsUseCase useCase;
    private final Context context;
    public GetEventsEndPoint(GetEventsUseCase useCase, Context context) {
        this.useCase = useCase;
        this.context = context;
    }
    @Override
    public void execute() {
        this.context.result(String.join("\n", this.useCase.execute()));
    }
}
