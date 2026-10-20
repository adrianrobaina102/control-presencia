package software.ulpgc.controlpresencia.model;

import java.time.LocalDate;

public record Contact(String telephone, String email, LocalDate date) {
}
