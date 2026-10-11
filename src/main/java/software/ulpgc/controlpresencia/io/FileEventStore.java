package software.ulpgc.controlpresencia.io;

import software.ulpgc.controlpresencia.model.TrackingEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
    private File getFileFor(TrackingEvent event)  {
        return new File(new File(root, String.valueOf(getDateTimeFor(event).getYear())), getName(event));
    }
    private LocalDateTime getDateTimeFor(TrackingEvent event)  {
        return LocalDateTime.ofInstant(event.ts(), ZoneId.systemDefault());
    }
    private String getName(TrackingEvent event)
    {
        return getDateTimeFor(event).format(DateTimeFormatter.ofPattern("MMyyyy")) + ".log";
    }
}
