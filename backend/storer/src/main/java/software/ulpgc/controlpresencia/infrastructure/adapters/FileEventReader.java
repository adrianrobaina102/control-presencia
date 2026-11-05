package software.ulpgc.controlpresencia.infrastructure.adapters;

import software.ulpgc.controlpresencia.application.ports.EventReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

public class FileEventReader implements EventReader {
    private final File root;

    public FileEventReader(File root) {
        this.root = root;
    }

    @Override
    public List<String> read() {
        if (!root.exists()) return List.of();
        try (Stream<java.nio.file.Path> stream = Files.walk(root.toPath())) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".log"))
                    .flatMap(path -> {
                        try {
                            return Files.readAllLines(path).stream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
