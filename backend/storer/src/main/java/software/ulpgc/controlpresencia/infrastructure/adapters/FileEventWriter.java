package software.ulpgc.controlpresencia.infrastructure.adapters;

import software.ulpgc.controlpresencia.architecture.ports.EventWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileEventWriter implements EventWriter {

    private final File root;

    public FileEventWriter(File root) {
        this.root = root;
    }

    @Override
    public void write(String eventText) {
        try {
            getMonthFolder(eventText).mkdirs();
            Files.writeString(getFileFor(eventText).toPath(), formatRow(eventText), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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
        return LocalDateTime.ofInstant(Instant.parse(extractTimestamp(eventText)), ZoneId.systemDefault());
    }

    private String extractTimestamp(String eventText) {
        return eventText.substring(0, eventText.indexOf(";"));
    }

    private String formatRow(String eventText) {
        return eventText + System.lineSeparator();
    }

    private String formatDate(String eventText, String pattern) {
        return getDateTimeFor(eventText).format(DateTimeFormatter.ofPattern(pattern));
    }
}
