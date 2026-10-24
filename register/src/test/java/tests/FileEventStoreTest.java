package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.controlpresencia.ports.EventStore;
import software.ulpgc.controlpresencia.adapters.FileEventStore;
import software.ulpgc.controlpresencia.model.*;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class FileEventStoreTest {

    @TempDir
    Path tempDir;

    @Test
    public void should_create_log_file_in_directory_matching_event_date() {
        EventStore store = new FileEventStore(tempDir.toFile());
        store.register(createSampleTrackingEvent());

        Path expectedLogFile = tempDir.resolve("2026").resolve("10").resolve("20261013.log");

        assertThat(expectedLogFile).exists().isRegularFile();
    }

    @Test
    public void should_write_employee_and_place_data_in_log_file() {
        EventStore store = new FileEventStore(tempDir.toFile());
        store.register(createSampleTrackingEvent());

        Path expectedLogFile = tempDir.resolve("2026").resolve("10").resolve("20261013.log");

        assertThat(expectedLogFile).content()
                .contains("emp-01")
                .contains("Sede Central")
                .contains("In");
    }
    @Test
    public void should_append_multiple_events_to_the_same_log_file() {
        EventStore store = new FileEventStore(tempDir.toFile());

        TrackingEvent firstEvent = createSampleTrackingEvent();
        TrackingEvent secondEvent = createSampleTrackingEvent();

        store.register(firstEvent);
        store.register(secondEvent);

        Path expectedLogFile = tempDir.resolve("2026").resolve("10").resolve("20261013.log");

        assertThat(expectedLogFile).content()
                .hasLineCount(2);
    }
    private TrackingEvent createSampleTrackingEvent() {
        Contact contact = new Contact("600000000", "adrian@ulpgc.es", LocalDate.of(2026, 1, 1));
        Employee employee = new Employee("emp-01", "Adrián", contact);
        Place place = new Place("hq-01", "Sede Central");
        Instant timestamp = Instant.parse("2026-10-13T08:30:00Z");

        return new TrackingEvent(timestamp, employee, place, TrackingType.In);
    }
}