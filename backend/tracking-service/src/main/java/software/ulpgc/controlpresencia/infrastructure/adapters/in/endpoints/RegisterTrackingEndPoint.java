package software.ulpgc.controlpresencia.infrastructure.adapters.in.endpoints;

import io.javalin.http.Context;
import software.ulpgc.controlpresencia.architecture.ports.in.DeviceReader;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventParser;
import software.ulpgc.controlpresencia.architecture.domain.RegisterPresenceUseCase;

public class RegisterTrackingEndPoint implements EndPoint {
    private final RegisterPresenceUseCase useCase;
    private final Context context;

    public RegisterTrackingEndPoint(RegisterPresenceUseCase useCase, Context context) {
        this.useCase = useCase;
        this.context = context;
    }

    @Override
    public void execute() {
        useCase.execute(null);
        context.status(201).result("Tracking event registered");
    }
}
