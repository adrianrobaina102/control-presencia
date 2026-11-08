package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.domain.ListEmployeesUseCase;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ListEmployeesUseCaseTest {

    @Test
    public void should_return_all_employees() {
        Employee emp1 = createSampleEmployee("emp-01", "Adrián Robaina");
        Employee emp2 = createSampleEmployee("emp-02", "María Comercio");

        EmployeeReader mockReader = new EmployeeReader() {
            @Override
            public Optional<Employee> get(String id) {
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return List.of(emp1, emp2);
            }
        };

        ListEmployeesUseCase useCase = new ListEmployeesUseCase(mockReader);
        List<Employee> result = useCase.execute();

        assertThat(result).hasSize(2)
                .extracting(Employee::name)
                .containsExactly("Adrián Robaina", "María Comercio");
    }

    private Employee createSampleEmployee(String id, String name) {
        Contact contact = new Contact("email@empresa.com", "+34 600 000 000");
        Contract contract = new Contract("CTR-001", "SCH-OFFICE", LocalDate.of(2026, 1, 1), null, ContractStatus.ACTIVE);
        return new Employee(id, name, contact, Role.EMPLOYEE, contract);
    }
}
