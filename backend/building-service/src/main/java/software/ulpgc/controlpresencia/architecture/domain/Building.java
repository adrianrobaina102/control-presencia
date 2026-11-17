package software.ulpgc.controlpresencia.architecture.domain;

public record Building(String id, String name, String address, Organization organization, boolean active) {
}
