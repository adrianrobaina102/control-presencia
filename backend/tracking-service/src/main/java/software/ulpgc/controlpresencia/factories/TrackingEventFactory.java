package software.ulpgc.controlpresencia.factories;

import net.datafaker.Faker;
import software.ulpgc.controlpresencia.architecture.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TrackingEventFactory {
    private static final Random random = new Random();
    private static final Faker faker = new Faker(new Locale("es"));

    private static final List<Place> places = List.of(
            new Place("PLC-01", "Oficina Central"),
            new Place("PLC-02", "Sede Sur"),
            new Place("PLC-03", "Teletrabajo")
    );

    public static TrackingEvent createTrackingEvent() {
        return new TrackingEvent(Instant.now(), randomEmployee(), randomPlace(), randomType());
    }

    private static Employee randomEmployee() {
        return new Employee(faker.idNumber().valid(), faker.name().fullName(), randomContact());
    }

    private static Contact randomContact() {
        return new Contact(faker.internet().emailAddress(), faker.phoneNumber().cellPhone(), LocalDate.now());
    }

    private static Place randomPlace() {
        return places.get(random.nextInt(places.size()));
    }

    private static TrackingType randomType() {
        return random.nextBoolean() ? TrackingType.In : TrackingType.Out;
    }
}
