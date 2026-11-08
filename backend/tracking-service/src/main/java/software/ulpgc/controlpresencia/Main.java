package software.ulpgc.controlpresencia;

import io.javalin.Javalin;
import software.ulpgc.controlpresencia.architecture.model.Configuration;
import software.ulpgc.controlpresencia.architecture.ports.in.ConfigurationReader;
import software.ulpgc.controlpresencia.architecture.ports.in.DeviceReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventPublisher;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventParser;
import software.ulpgc.controlpresencia.architecture.domain.RegisterPresenceUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.JsonConfigurationReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.SwingDeviceReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.endpoints.RegisterPresenceEndPoint;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.out.ActiveMQEventPublisher;
import software.ulpgc.controlpresencia.infrastructure.adapters.processing.CsvTrackingEventParser;
import software.ulpgc.controlpresencia.infrastructure.adapters.processing.CsvTrackingEventSerializer;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        File configFile = new File("backend/tracking-service/configuration.json");
        if (!configFile.exists()) {
            configFile = new File("tracking-service/configuration.json");
        }
        ConfigurationReader configReader = new JsonConfigurationReader(configFile);
        Configuration config = configReader.read();

        ActiveMQBrokerConnection brokerConnection = new ActiveMQBrokerConnection(config.brokerUrl());
        EventPublisher publisher = new ActiveMQEventPublisher(brokerConnection.getSession(), "fichajes.queue", new CsvTrackingEventSerializer());
        TrackingEventParser parser = new CsvTrackingEventParser();

        SwingDeviceReader deviceReader = new SwingDeviceReader();
        RegisterPresenceUseCase swingUseCase = new RegisterPresenceUseCase(publisher, deviceReader, parser);
        deviceReader.execute(e -> swingUseCase.execute(null));

        Javalin app = Javalin.create().start(7070);
        app.post("/events", context -> {
            DeviceReader httpReader = () -> context.queryParam("event");
            RegisterPresenceUseCase httpUseCase = new RegisterPresenceUseCase(publisher, httpReader, parser);
            new RegisterPresenceEndPoint(httpUseCase, context).execute();
        });
        app.get("/events", context -> {
            DeviceReader httpReader = () -> context.queryParam("event");
            RegisterPresenceUseCase httpUseCase = new RegisterPresenceUseCase(publisher, httpReader, parser);
            new RegisterPresenceEndPoint(httpUseCase, context).execute();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                brokerConnection.close();
                app.stop();
            } catch (Exception ignored) {}
        }));
    }
}
