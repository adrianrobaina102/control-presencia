package software.ulpgc.controlpresencia.io;

import software.ulpgc.controlpresencia.model.TrackingEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileEventStore implements EventStore {
    private final File root;
    public FileEventStore(File root) {
        this.root = root;
    }
    @Override
    public void register(TrackingEvent event) {
        String row = serializa(event) + System.lineSeparator();
        try {
            Files.writeString(root.toPath(), row, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private String serializa(TrackingEvent event) {
        return event.ts() + ";"+ event.employee().id()  + ";" + event.place().id()  + ";" + event.type();
    }
}
