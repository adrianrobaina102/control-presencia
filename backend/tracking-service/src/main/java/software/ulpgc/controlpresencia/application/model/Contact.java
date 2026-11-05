package software.ulpgc.controlpresencia.application.model;

import java.time.LocalDate;

public record Contact(String telephone, String email, LocalDate date) {
}
