package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.application.model.Employee;
import software.ulpgc.controlpresencia.application.model.Place;
import software.ulpgc.controlpresencia.application.model.TrackingEvent;
import software.ulpgc.controlpresencia.application.model.TrackingType;
import software.ulpgc.controlpresencia.application.ports.EventStore;
import software.ulpgc.controlpresencia.application.usecases.RegisterTrackingEventUseCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterTrackingEventUseCaseTest {

    @Test
    public void should_register_tracking_event_via_event_store_port() {
        List<TrackingEvent> recordedEvents = new ArrayList<>();
        EventStore mockEventStore = recordedEvents::add;

        RegisterTrackingEventUseCase useCase = new RegisterTrackingEventUseCase(mockEventStore);

        Employee employee = new Employee("emp-101");
        Place place = new Place("Sede Central");
        TrackingEvent sampleEvent = new TrackingEvent(employee, place, TrackingType.In);

        Void result = useCase.execute(sampleEvent);

        assertThat(result).isNull();
        assertThat(recordedEvents)
                .hasSize(1)
                .containsExactly(sampleEvent);
    }
}
