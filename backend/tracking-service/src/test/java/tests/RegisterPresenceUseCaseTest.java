package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.model.Employee;
import software.ulpgc.controlpresencia.architecture.model.Place;
import software.ulpgc.controlpresencia.architecture.model.TrackingEvent;
import software.ulpgc.controlpresencia.architecture.model.TrackingType;
import software.ulpgc.controlpresencia.architecture.ports.in.DeviceReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventPublisher;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventParser;
import software.ulpgc.controlpresencia.architecture.domain.RegisterPresenceUseCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPresenceUseCaseTest {

    @Test
    public void should_read_from_device_parse_and_publish_event() {
        DeviceReader fakeReader = () -> "emp-01;Oficina;In";
        TrackingEventParser fakeParser = input -> {
            String[] parts = input.split(";");
            return new TrackingEvent(new Employee(parts[0]), new Place(parts[1]), TrackingType.valueOf(parts[2]));
        };
        List<TrackingEvent> recordedEvents = new ArrayList<>();
        EventPublisher fakePublisher = recordedEvents::add;

        RegisterPresenceUseCase useCase = new RegisterPresenceUseCase(fakePublisher, fakeReader, fakeParser);
        useCase.execute(null);

        assertThat(recordedEvents).hasSize(1);
        TrackingEvent recorded = recordedEvents.get(0);
        assertThat(recorded.employee().id()).isEqualTo("emp-01");
        assertThat(recorded.place().name()).isEqualTo("Oficina");
        assertThat(recorded.type()).isEqualTo(TrackingType.In);
    }
}
