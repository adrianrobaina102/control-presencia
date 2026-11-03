package software.ulpgc.controlpresencia;

import io.javalin.Javalin;
import software.ulpgc.controlpresencia.application.ports.EventStore;
import software.ulpgc.controlpresencia.application.usecases.RegisterTrackingEventUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventStore;
import software.ulpgc.controlpresencia.infrastructure.adapters.endpoints.RegisterTrackingEndPoint;

public class Main {
    public static void main(String[] args) throws Exception {
        ActiveMQBrokerConnection brokerConnection = new ActiveMQBrokerConnection();
        EventStore store = new ActiveMQEventStore(brokerConnection.getSession(), "fichajes.queue");

        RegisterTrackingEventUseCase useCase = new RegisterTrackingEventUseCase(store);

        Javalin app = Javalin.create().start(7070);

        app.post("/events", context -> new RegisterTrackingEndPoint(useCase, context).execute());
        app.get("/events", context -> new RegisterTrackingEndPoint(useCase, context).execute());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                brokerConnection.close();
                app.stop();
            } catch (Exception ignored) {}
        }));
    }
}
