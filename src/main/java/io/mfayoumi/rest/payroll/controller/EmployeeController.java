package io.mfayoumi.rest.payroll.controller;

import io.mfayoumi.rest.payroll.EmployeeModelAssembler;
import io.mfayoumi.rest.payroll.entity.Employee;
import io.mfayoumi.rest.payroll.exception.EmployeeNotFoundException;
import io.mfayoumi.rest.payroll.repository.EmployeeRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
  private static final String UPDATE_ON_PUT = "UpdateOnPUT-ResponseEntityBuilderWithHttpHeaders";
  private static final String CREATE_ON_PUT = "CreatedOnUpdate-ResponseEntityBuilderWithHttpHeaders";

  private final EmployeeRepository repository;
  private final EmployeeModelAssembler assembler;

  public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }

  @GetMapping
  public CollectionModel<EntityModel<Employee>> all() {
    List<EntityModel<Employee>> employees = repository.findAll().stream()
            .map(this.assembler::toModel)
            .collect(Collectors.toList());
    return CollectionModel.of(employees,
            linkTo(EmployeeController.class).withRel("employees"));
  }

  @GetMapping("/{id}")
  public EntityModel<Employee> getEmployeeById(@PathVariable Long id) {
    Employee employee = repository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

    return this.assembler.toModel(employee);
  }

  @PostMapping
  public ResponseEntity<?> addEmployee(@RequestBody Employee newEmployee) {
    EntityModel<Employee> entityModel = this.assembler.toModel(repository.save(newEmployee));
    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> replaceEmployee(@RequestBody Employee updatedEmployee, @PathVariable Long id) {
    Employee newUpdatedEmployee = this.repository.findById(id)
            .map(employee -> {
              employee.setName(updatedEmployee.getName());
              employee.setRole(updatedEmployee.getRole());
              return this.repository.save(employee);
            })
            .orElseGet(() -> {
              updatedEmployee.setId(id);
              return this.repository.save(updatedEmployee);
            });
    EntityModel<Employee> entityModel = this.assembler.toModel(newUpdatedEmployee);
    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel);
  }

  @PutMapping("/old/{id}")
  public ResponseEntity<Employee> updateEmployee(@RequestBody Employee updatedEmployee, @PathVariable Long id) {
    HttpHeaders responseHeaders = new HttpHeaders();
    Employee employee = this.repository.findById(id)
            .map(employee1 -> {
              employee1.setName(updatedEmployee.getName());
              employee1.setRole(updatedEmployee.getRole());
              responseHeaders.set("Employee-PUT-Header", UPDATE_ON_PUT);
              return this.repository.save(employee1);
            }).orElseGet(() -> {
              responseHeaders.set("Employee-PUT-Header", CREATE_ON_PUT);
              return this.repository.save(updatedEmployee);
            });
    return ResponseEntity.ok().headers(responseHeaders).body(employee);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
    this.repository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
