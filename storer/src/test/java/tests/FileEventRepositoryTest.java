package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.controlpresencia.application.ports.EventRepository;
import software.ulpgc.controlpresencia.infrastructure.adapters.FileEventRepository;

import javax.jms.JMSException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class FileEventRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    public void should_create_log_file_in_directory_matching_event_date() throws JMSException {
        EventRepository repository = new FileEventRepository(tempDir.toFile());
        repository.save(createSampleEventText());

        Path expectedLogFile = tempDir.resolve("2026").resolve("10").resolve("20261013.log");

        assertThat(expectedLogFile).exists().isRegularFile();
    }

    @Test
    public void should_write_employee_and_place_data_in_log_file() throws JMSException {
        EventRepository repository = new FileEventRepository(tempDir.toFile());
        repository.save(createSampleEventText());

        Path expectedLogFile = tempDir.resolve("2026").resolve("10").resolve("20261013.log");

        assertThat(expectedLogFile).content()
                .contains("emp-01")
                .contains("Sede Central")
                .contains("In");
    }

    @Test
    public void should_append_multiple_events_to_the_same_log_file() throws JMSException {
        EventRepository repository = new FileEventRepository(tempDir.toFile());

        repository.save(createSampleEventText());
        repository.save(createSampleEventText());

        Path expectedLogFile = tempDir.resolve("2026").resolve("10").resolve("20261013.log");

        assertThat(expectedLogFile).content()
                .hasLineCount(2);
    }

    private String createSampleEventText() {
        return "2026-10-13T08:30:00Z;emp-01;Sede Central;In";
    }
}