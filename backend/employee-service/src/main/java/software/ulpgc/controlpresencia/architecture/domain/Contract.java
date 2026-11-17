package software.ulpgc.controlpresencia.architecture.domain;

import java.time.LocalDate;

public record Contract(String id, String scheduleId, LocalDate startDate, LocalDate endDate, ContractStatus status) {
}
