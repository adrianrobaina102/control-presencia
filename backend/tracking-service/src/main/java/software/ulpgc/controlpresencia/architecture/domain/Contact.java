package software.ulpgc.controlpresencia.architecture.domain;

import java.time.LocalDate;

public record Contact(String telephone, String email, LocalDate date) {
}
