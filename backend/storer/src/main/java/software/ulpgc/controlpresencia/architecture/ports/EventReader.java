package software.ulpgc.controlpresencia.architecture.ports;

import java.util.List;

public interface EventReader {
    List<String> read();
}
