package io.mfayoumi.rest.payroll.repository;

import io.mfayoumi.rest.payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
