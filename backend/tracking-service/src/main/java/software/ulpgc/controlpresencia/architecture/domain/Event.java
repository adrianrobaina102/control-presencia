package software.ulpgc.controlpresencia.architecture.domain;

import java.time.Instant;

public interface Event {
    Instant ts();
}
