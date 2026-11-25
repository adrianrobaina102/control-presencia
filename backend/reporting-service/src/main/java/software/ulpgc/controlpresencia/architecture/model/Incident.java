package software.ulpgc.controlpresencia.architecture.domain;

public record Incident(String description, int minutes, IncidentType incidentTypee) {
}
