package software.ulpgc.controlpresencia.architecture.model;

public record Place(String id, String name) {
    public Place(String name) {
        this(name, name);
    }
}
