package software.ulpgc.controlpresencia.infrastructure.adapters.endpoints;

import io.javalin.http.Context;
import software.ulpgc.controlpresencia.application.model.Employee;
import software.ulpgc.controlpresencia.application.model.Place;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.model.TrackingType;
import software.ulpgc.controlpresencia.application.usecases.RegisterTrackingEventUseCase;

public class RegisterTrackingEndPoint implements EndPoint {
    private final RegisterTrackingEventUseCase useCase;
    private final Context context;

    public RegisterTrackingEndPoint(RegisterTrackingEventUseCase useCase, Context context) {
        this.useCase = useCase;
        this.context = context;
    }

    @Override
    public void execute() {
        Employee employee = new Employee(context.queryParam("employeeId"));
        Place place = new Place(context.queryParam("placeId"));
        useCase.execute(new TrackingEvent(employee, place, TrackingType.valueOf(context.queryParam("type"))));
        context.status(201).result("Tracking event registered");
    }
}
