package software.ulpgc.controlpresencia.architecture.model;

import java.time.LocalDate;

public record Contract(String id, String scheduleId, LocalDate startDate, LocalDate endDate, ContractStatus status) {
}
