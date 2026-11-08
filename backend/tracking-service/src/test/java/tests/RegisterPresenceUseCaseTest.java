package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.Employee;
import software.ulpgc.controlpresencia.architecture.domain.Place;
import software.ulpgc.controlpresencia.architecture.domain.TrackingEvent;
import software.ulpgc.controlpresencia.architecture.domain.TrackingType;
import software.ulpgc.controlpresencia.architecture.ports.in.DeviceReader;
import software.ulpgc.controlpresencia.architecture.ports.out.EventStore;
import software.ulpgc.controlpresencia.architecture.ports.processing.TrackingEventParser;
import software.ulpgc.controlpresencia.architecture.domain.RegisterPresenceUseCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPresenceUseCaseTest {

    @Test
    public void should_read_from_device_parse_and_store_event() {
        DeviceReader fakeReader = () -> "emp-01;Oficina;In";
        TrackingEventParser fakeParser = input -> {
            String[] parts = input.split(";");
            return new TrackingEvent(new Employee(parts[0]), new Place(parts[1]), TrackingType.valueOf(parts[2]));
        };
        List<TrackingEvent> recordedEvents = new ArrayList<>();
        EventStore fakeStore = recordedEvents::add;

        RegisterPresenceUseCase useCase = new RegisterPresenceUseCase(fakeStore, fakeReader, fakeParser);
        useCase.execute(null);

        assertThat(recordedEvents).hasSize(1);
        TrackingEvent recorded = recordedEvents.get(0);
        assertThat(recorded.employee().id()).isEqualTo("emp-01");
        assertThat(recorded.place().name()).isEqualTo("Oficina");
        assertThat(recorded.type()).isEqualTo(TrackingType.In);
    }
}
