package software.ulpgc.controlpresencia;

import io.javalin.Javalin;
import software.ulpgc.controlpresencia.application.model.Configuration;
import software.ulpgc.controlpresencia.application.ports.in.ConfigurationReader;
import software.ulpgc.controlpresencia.application.ports.in.DeviceReader;
import software.ulpgc.controlpresencia.application.ports.out.EventStore;
import software.ulpgc.controlpresencia.application.ports.processing.TrackingEventParser;
import software.ulpgc.controlpresencia.application.usecases.RegisterPresenceUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.JsonConfigurationReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.MockDeviceReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.ActiveMQEventStore;
import software.ulpgc.controlpresencia.infrastructure.adapters.processing.CsvTrackingEventParser;
import software.ulpgc.controlpresencia.infrastructure.adapters.processing.CsvTrackingEventSerializer;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        ConfigurationReader configReader = new JsonConfigurationReader(new File("tracking-service/configuration.json"));
        Configuration config = configReader.read();

        ActiveMQBrokerConnection brokerConnection = new ActiveMQBrokerConnection(config.brokerUrl());
        EventStore store = new ActiveMQEventStore(brokerConnection.getSession(), "fichajes.queue", new CsvTrackingEventSerializer());
        TrackingEventParser parser = new CsvTrackingEventParser();

        MockDeviceReader deviceReader = new MockDeviceReader();
        RegisterPresenceUseCase swingUseCase = new RegisterPresenceUseCase(store, deviceReader, parser);
        deviceReader.execute(e -> swingUseCase.execute(null));

        Javalin app = Javalin.create().start(7070);
        app.post("/events", context -> {
            DeviceReader httpReader = () -> context.queryParam("event");
            new RegisterPresenceUseCase(store, httpReader, parser).execute(null);
            context.status(201).result("Tracking event registered");
        });
        app.get("/events", context -> {
            DeviceReader httpReader = () -> context.queryParam("event");
            new RegisterPresenceUseCase(store, httpReader, parser).execute(null);
            context.status(201).result("Tracking event registered");
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                brokerConnection.close();
                app.stop();
            } catch (Exception ignored) {}
        }));
    }
}
