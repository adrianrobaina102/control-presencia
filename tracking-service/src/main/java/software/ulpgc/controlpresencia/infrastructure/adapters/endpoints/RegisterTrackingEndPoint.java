package software.ulpgc.controlpresencia.infrastructure.adapters.endpoints;

import io.javalin.http.Context;
import software.ulpgc.controlpresencia.application.model.Employee;
import software.ulpgc.controlpresencia.application.model.Place;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.model.TrackingType;
import software.ulpgc.controlpresencia.application.ports.TrackingEventParser;
import software.ulpgc.controlpresencia.application.usecases.RegisterTrackingEventUseCase;

public class RegisterTrackingEndPoint implements EndPoint {
    private final RegisterTrackingEventUseCase useCase;
    private final Context context;
    private final TrackingEventParser eventParser;

    public RegisterTrackingEndPoint(RegisterTrackingEventUseCase useCase, Context context, TrackingEventParser eventParser) {
        this.useCase = useCase;
        this.context = context;
        this.eventParser = eventParser;
    }

    @Override
    public void execute() {
        useCase.execute(this.eventParser.parse(context.queryParam("event")));
        context.status(201).result("Tracking event registered");
    }
}
