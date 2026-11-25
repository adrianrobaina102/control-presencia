package software.ulpgc.controlpresencia.architecture.model;

public record Incident(String description, int minutes, IncidentType type) {
}
