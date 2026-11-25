package software.ulpgc.controlpresencia.architecture.domain;

import java.time.LocalDate;

public record Contract(String id, String scheduleId, LocalDate startDate, LocalDate endDate, ContractStatus status) {
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private String id;
        private String scheduleId;
        private LocalDate startDate;
        private LocalDate endDate;
        private ContractStatus status;
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder scheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
            return this;
        }
        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }
        public Builder status(ContractStatus status) {
            this.status = status;
            return this;
        }
        public Contract build() {
            return new Contract(id, scheduleId, startDate, endDate, status);
        }
    }
}
