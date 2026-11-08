package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.ports.EventWriter;
import software.ulpgc.controlpresencia.architecture.domain.StoreEventUseCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreEventUseCaseTest {

    @Test
    public void should_store_event_via_event_writer_port() {
        List<String> storedEvents = new ArrayList<>();
        EventWriter mockWriter = storedEvents::add;

        StoreEventUseCase useCase = new StoreEventUseCase(mockWriter);

        String sampleEvent = "2026-10-31T15:00:00Z;emp-01;Oficina;In";
        Void result = useCase.execute(sampleEvent);

        assertThat(result).isNull();
        assertThat(storedEvents)
                .hasSize(1)
                .containsExactly(sampleEvent);
    }
}
