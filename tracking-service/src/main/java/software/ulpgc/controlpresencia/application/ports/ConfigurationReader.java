package software.ulpgc.controlpresencia.application.ports;

import software.ulpgc.controlpresencia.application.model.Configuration;

public interface ConfigurationReader {
    Configuration read();
}
