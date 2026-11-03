package software.ulpgc.controlpresencia;

import io.javalin.Javalin;
import software.ulpgc.controlpresencia.application.model.Configuration;
import software.ulpgc.controlpresencia.application.ports.in.ConfigurationReader;
import software.ulpgc.controlpresencia.application.ports.out.EventStore;
import software.ulpgc.controlpresencia.application.ports.processing.TrackingEventParser;
import software.ulpgc.controlpresencia.application.ports.processing.TrackingEventSerializer;
import software.ulpgc.controlpresencia.application.usecases.RegisterTrackingEventUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.JsonConfigurationReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.ActiveMQEventStore;
import software.ulpgc.controlpresencia.infrastructure.adapters.processing.CsvTrackingEventParser;
import software.ulpgc.controlpresencia.infrastructure.adapters.processing.CsvTrackingEventSerializer;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.endpoints.RegisterTrackingEndPoint;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        ConfigurationReader configReader = new JsonConfigurationReader(new File("tracking-service/configuration.json"));
        Configuration config = configReader.read();

        ActiveMQBrokerConnection brokerConnection = new ActiveMQBrokerConnection(config.brokerUrl());
        EventStore store = new ActiveMQEventStore(brokerConnection.getSession(), "fichajes.queue", new CsvTrackingEventSerializer());

        RegisterTrackingEventUseCase useCase = new RegisterTrackingEventUseCase(store);

        Javalin app = Javalin.create().start(7070);
        TrackingEventParser eventParser = new CsvTrackingEventParser();
        app.post("/events", context -> new RegisterTrackingEndPoint(useCase, context, eventParser).execute());
        app.get("/events", context -> new RegisterTrackingEndPoint(useCase, context, eventParser).execute());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                brokerConnection.close();
                app.stop();
            } catch (Exception ignored) {}
        }));
    }
}
