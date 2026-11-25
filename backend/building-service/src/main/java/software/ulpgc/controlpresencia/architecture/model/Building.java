package software.ulpgc.controlpresencia.architecture.model;

public record Building(String id, String name, String address, Organization organization, boolean active) {
}
