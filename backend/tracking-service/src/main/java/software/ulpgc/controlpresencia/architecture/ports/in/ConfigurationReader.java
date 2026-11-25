package software.ulpgc.controlpresencia.architecture.ports.in;

import software.ulpgc.controlpresencia.architecture.model.Configuration;

public interface ConfigurationReader {
    Configuration read();
}
