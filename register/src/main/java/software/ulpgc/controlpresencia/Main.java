package software.ulpgc.controlpresencia;

import software.ulpgc.controlpresencia.adapters.FileEventStore;
import software.ulpgc.controlpresencia.model.*;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Contact contact = new Contact("666555444", "adri@correo.com", LocalDate.now());
        Employee employee = new Employee("EMP-001", "Adri", contact);
        Place place = new Place("PLC-01", "Oficina Central");

        TrackingEvent trackingEvent = new TrackingEvent(Instant.now(), employee, place, TrackingType.In);

        File file = new File("store");
        FileEventStore fileEventStore = new FileEventStore(file);
        fileEventStore.register(trackingEvent);
    }
}
