package software.ulpgc.controlpresencia;

import io.javalin.Javalin;
import software.ulpgc.controlpresencia.application.ports.EventReceiver;
import software.ulpgc.controlpresencia.application.ports.EventRepository;
import software.ulpgc.controlpresencia.application.usecases.StoreEventUseCase;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQBrokerConnection;
import software.ulpgc.controlpresencia.infrastructure.adapters.ActiveMQEventReceiver;
import software.ulpgc.controlpresencia.infrastructure.adapters.FileEventRepository;
import software.ulpgc.controlpresencia.infrastructure.adapters.endpoints.StoreEventEndPoint;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        ActiveMQBrokerConnection connection = new ActiveMQBrokerConnection();
        EventReceiver receiver = new ActiveMQEventReceiver(connection.getSession(), "fichajes.queue");
        EventRepository repository = new FileEventRepository(new File("store"));

        StoreEventUseCase useCase = new StoreEventUseCase(repository);

        Javalin app = Javalin.create().start(7071);

        app.post("/events", context -> new StoreEventEndPoint(useCase, context).execute());
        app.get("/events", context -> new StoreEventEndPoint(useCase, context).execute());

        System.out.println("Storer encendido. Escuchando nuevos fichajes de la cola...");
        String message;
        while ((message = receiver.receive()) != null) {
            System.out.println("Message received from queue: " + message);
            useCase.execute(message);
        }
        System.out.println("Cola vacia. Servidor HTTP activo en puerto 7071.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
                app.stop();
            } catch (Exception ignored) {}
        }));
    }
}
