package software.ulpgc.controlpresencia.architecture.domain;

public record Place(String id, String name) {
    public Place(String name) {
        this(name, name);
    }
}
