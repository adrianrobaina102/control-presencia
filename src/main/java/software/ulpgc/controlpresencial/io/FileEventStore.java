package software.ulpgc.controlpresencial.io;

import software.ulpgc.controlpresencial.model.TrackingEvent;

import java.io.File;

public class FileEventStore implements EventStore {
    private final File root;
    public FileEventStore(File root) {
        this.root = root;
    }
    @Override
    public void register(TrackingEvent event) {

    }
}
