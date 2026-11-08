package software.ulpgc.controlpresencia.infrastructure.adapters.in;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import software.ulpgc.controlpresencia.architecture.model.Configuration;
import software.ulpgc.controlpresencia.architecture.ports.in.ConfigurationReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonConfigurationReader implements ConfigurationReader {
    private final File configurationFile;

    public JsonConfigurationReader(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public Configuration read() {
        try {
            String jsonString = Files.readString(configurationFile.toPath());
            return new Gson().fromJson(jsonString, Configuration.class);
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException("Could not read configuration from " + configurationFile.getPath(), e);
        }
    }
}
