package software.ulpgc.controlpresencia.infrastructure.adapters.endpoints;

import io.javalin.http.Context;
import software.ulpgc.controlpresencia.application.usecases.StoreEventUseCase;

public class StoreEventEndPoint implements EndPoint {
    private final StoreEventUseCase useCase;
    private final Context context;

    public StoreEventEndPoint(StoreEventUseCase useCase, Context context) {
        this.useCase = useCase;
        this.context = context;
    }

    @Override
    public void execute() {
        execute(context);
    }

    private void execute(Context context) {
        String eventText = context.body().isBlank() ? context.queryParam("event") : context.body();
        execute(eventText);
    }

    private void execute(String eventText) {
        if (eventText == null || eventText.isBlank()) {
            execute(400, "Event text is required");
            return;
        }

        useCase.execute(eventText);
        execute(201, "Event stored successfully");
    }

    private void execute(int statusCode, String message) {
        context.status(statusCode).result(message);
    }
}
