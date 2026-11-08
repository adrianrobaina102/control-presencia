package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.controlpresencia.architecture.domain.Configuration;
import software.ulpgc.controlpresencia.architecture.ports.in.ConfigurationReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.in.JsonConfigurationReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JsonConfigurationReaderTest {

    @Test
    public void should_read_configuration_from_json_file(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "broker-url": "tcp://localhost:61616",
                  "broker-token": "test-token",
                  "employee-service-url": "http://localhost:7072",
                  "building-service-url": "http://localhost:7073"
                }
                """;
        Path configFile = tempDir.resolve("config.json");
        Files.writeString(configFile, json);

        ConfigurationReader reader = new JsonConfigurationReader(configFile.toFile());
        Configuration config = reader.read();

        assertThat(config.brokerUrl()).isEqualTo("tcp://localhost:61616");
        assertThat(config.brokerToken()).isEqualTo("test-token");
        assertThat(config.employeeServiceUrl()).isEqualTo("http://localhost:7072");
        assertThat(config.buildingServiceUrl()).isEqualTo("http://localhost:7073");
    }

    @Test
    public void should_throw_runtime_exception_when_file_not_found() {
        ConfigurationReader reader = new JsonConfigurationReader(new File("non_existent_file.json"));

        assertThatThrownBy(reader::read)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not read configuration");
    }
}
