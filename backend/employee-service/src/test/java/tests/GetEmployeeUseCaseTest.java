package tests;

import org.junit.jupiter.api.Test;
import software.ulpgc.controlpresencia.architecture.usecases.GetEmployeeUseCase;
import software.ulpgc.controlpresencia.architecture.model.*;
import software.ulpgc.controlpresencia.architecture.ports.EmployeeReader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GetEmployeeUseCaseTest {

    @Test
    public void should_return_employee_when_employee_exists() {
        Employee sampleEmployee = createSampleEmployee("emp-01", "Adrián Robaina");
        EmployeeReader mockReader = new EmployeeReader() {
            @Override
            public Optional<Employee> get(String id) {
                return "emp-01".equals(id) ? Optional.of(sampleEmployee) : Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return List.of(sampleEmployee);
            }
        };

        GetEmployeeUseCase useCase = new GetEmployeeUseCase(mockReader);
        Optional<Employee> result = useCase.execute("emp-01");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Adrián Robaina");
        assertThat(result.get().role()).isEqualTo(Role.EMPLOYEE);
        assertThat(result.get().contract().scheduleId()).isEqualTo("SCH-OFFICE-CONTINUOUS");
    }

    @Test
    public void should_return_empty_optional_when_employee_does_not_exist() {
        EmployeeReader emptyReader = new EmployeeReader() {
            @Override
            public Optional<Employee> get(String id) {
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return List.of();
            }
        };

        GetEmployeeUseCase useCase = new GetEmployeeUseCase(emptyReader);
        Optional<Employee> result = useCase.execute("non-existent");

        assertThat(result).isEmpty();
    }

    private Employee createSampleEmployee(String id, String name) {
        return Employee.builder()
                .id(id)
                .name(name)
                .contact(new Contact("adrian@empresa.com", "+34 600 111 222"))
                .role(Role.EMPLOYEE)
                .contract(Contract.builder()
                        .id("CTR-001")
                        .scheduleId("SCH-OFFICE-CONTINUOUS")
                        .startDate(LocalDate.of(2026, 1, 1))
                        .status(ContractStatus.ACTIVE)
                        .build())
                .build();
    }
}
