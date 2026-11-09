package software.ulpgc.controlpresencia;

import io.javalin.Javalin;
import software.ulpgc.controlpresencia.architecture.ports.EventReader;
import software.ulpgc.controlpresencia.architecture.ports.EventReceiver;
import software.ulpgc.controlpresencia.architecture.ports.EventWriter;
import software.ulpgc.controlpresencia.architecture.domain.GetEventsUseCase;
import software.ulpgc.controlpresencia.architecture.domain.StoreEventUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventReceiver;
import software.ulpgc.controlpresencia.infrastructure.adapters.FileEventReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.FileEventWriter;
import software.ulpgc.controlpresencia.infrastructure.adapters.endpoints.GetEventsEndPoint;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        ActiveMQBrokerConnection connection = new ActiveMQBrokerConnection();
        EventReceiver receiver = new ActiveMQEventReceiver(connection.getSession(), "fichajes.queue");
        File storeDir = new File("store");
        EventWriter writer = new FileEventWriter(storeDir);
        EventReader reader = new FileEventReader(storeDir);

        StoreEventUseCase storeUseCase = new StoreEventUseCase(writer);
        GetEventsUseCase getEventsUseCase = new GetEventsUseCase(reader);

        Javalin app = Javalin.create().start(7071);

        app.get("/", context -> new GetEventsEndPoint(getEventsUseCase, context).execute());
        app.get("/events", context -> new GetEventsEndPoint(getEventsUseCase, context).execute());

        System.out.println("Storer encendido. Escuchando nuevos fichajes de la cola...");
        String message;
        while ((message = receiver.receive()) != null) {
            System.out.println("Message received from queue: " + message);
            storeUseCase.execute(message);
        }
        System.out.println("Cola vacia. Servidor HTTP activo en puerto 7071.");
        System.out.println("-> Ver fichajes guardados: http://localhost:7071/");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
                app.stop();
            } catch (Exception ignored) {}
        }));
    }
}
