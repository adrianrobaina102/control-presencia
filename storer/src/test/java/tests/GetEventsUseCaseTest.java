package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.application.ports.EventReader;
import software.ulpgc.controlpresencia.application.usecases.GetEventsUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetEventsUseCaseTest {

    @Test
    public void should_return_all_events_from_event_reader_port() {
        List<String> sampleEvents = List.of(
                "2026-10-31T16:11:13Z;emp-01;Oficina-Central;In",
                "2026-10-31T16:12:59Z;emp-01;Oficina-Central;Out"
        );
        EventReader mockReader = () -> sampleEvents;

        GetEventsUseCase useCase = new GetEventsUseCase(mockReader);
        List<String> result = useCase.execute();

        assertThat(result)
                .hasSize(2)
                .containsExactlyElementsOf(sampleEvents);
    }
}
