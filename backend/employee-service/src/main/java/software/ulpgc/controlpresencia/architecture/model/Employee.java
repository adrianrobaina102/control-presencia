package software.ulpgc.controlpresencia.architecture.model;

public record Employee(String id, String name, Contact contact, Role role, Contract contract) {
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private String id;
        private String name;
        private Contact contact;
        private Role role;
        private Contract contract;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder contact(Contact contact) {
            this.contact = contact;
            return this;
        }
        public Builder role(Role role) {
            this.role = role;
            return this;
        }
        public Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        public Employee build() {
            return new Employee(id, name, contact, role, contract);
        }
    }
}
