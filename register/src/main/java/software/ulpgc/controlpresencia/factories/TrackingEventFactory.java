package software.ulpgc.controlpresencia.factories;

import net.datafaker.Faker;
import software.ulpgc.controlpresencia.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        String phone = faker.phoneNumber().cellPhone();
        String email = faker.internet().emailAddress();
        Contact contact = new Contact(email, phone, LocalDate.now());

        String id = faker.idNumber().valid();
        String name = faker.name().fullName();
        Employee employee = new Employee(id, name, contact);

        Place randomPlace = places.get(random.nextInt(places.size()));
        Instant randomTimestamp = Instant.now();
        TrackingType randomType = random.nextBoolean() ? TrackingType.In : TrackingType.Out;
        return new TrackingEvent(randomTimestamp, employee, randomPlace, randomType);
    }
}
