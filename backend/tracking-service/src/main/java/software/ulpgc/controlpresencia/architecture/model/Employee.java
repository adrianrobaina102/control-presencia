package software.ulpgc.controlpresencia.architecture.model;

public record Employee(String id, String name, Contact contact) {
    public Employee(String id) {
        this(id, "Desconocido", null);
    }
}
