package software.ulpgc.controlpresencia.application.model;

public record Employee(String id, String name, Contact contact) {
    public Employee(String id) {
        this(id, "Desconocido", null);
    }
}
