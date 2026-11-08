package software.ulpgc.controlpresencia.architecture.model;

public record Employee(String id, String name, Contact contact, Role role, Contract contract) {
}
