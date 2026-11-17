package software.ulpgc.controlpresencia.architecture.ports.in;

import software.ulpgc.controlpresencia.architecture.domain.Configuration;

public interface ConfigurationReader {
    Configuration read();
}
