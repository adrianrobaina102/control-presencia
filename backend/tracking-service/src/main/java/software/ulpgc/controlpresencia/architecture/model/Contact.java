package software.ulpgc.controlpresencia.architecture.model;

import java.time.LocalDate;

public record Contact(String telephone, String email, LocalDate date) {
}
