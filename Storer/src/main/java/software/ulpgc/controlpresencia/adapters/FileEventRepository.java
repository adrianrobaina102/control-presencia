package software.ulpgc.controlpresencia.adapters;

import software.ulpgc.controlpresencia.ports.EventRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileEventRepository implements EventRepository {

    private final File root;

    public FileEventRepository(File root) {
        this.root = root;
    }

    @Override
    public void save(String eventText) {
        String row = eventText + System.lineSeparator();
        File logFile = getFileFor(eventText);

        try {
            logFile.getParentFile().mkdirs();
            Files.writeString(logFile.toPath(), row, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFileFor(String eventText) {
        return new File(getMonthFolder(eventText), formatDate(eventText, "yyyyMMdd") + ".log");
    }

    private File getYearFolder(String eventText) {
        return new File(root, formatDate(eventText, "yyyy"));
    }

    private File getMonthFolder(String eventText) {
        return new File(getYearFolder(eventText), formatDate(eventText, "MM"));
    }

    private LocalDateTime getDateTimeFor(String eventText) {
        String timeStamp = eventText.substring(0, eventText.indexOf(";"));
        return LocalDateTime.ofInstant(Instant.parse(timeStamp), ZoneId.systemDefault());
    }


    private String formatDate(String eventText, String pattern) {
        return getDateTimeFor(eventText).format(DateTimeFormatter.ofPattern(pattern));
    }
}