package io.mfayoumi.rest.payroll.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "employee")
public class Employee {
  @Id
  @SequenceGenerator(
          name = "employee_sequence",
          sequenceName = "employee_sequence",
          allocationSize = 50
  )
  @Column(name = "id",
          updatable = false)
  @GeneratedValue(
          strategy = GenerationType.SEQUENCE,
          generator = "employee_sequence"
  )
  private Long id;

  @Column(
          name = "first_name",
          columnDefinition = "TEXT"
  )
  private String firstName;

  @Column(
          name = "last_name",
          columnDefinition = "TEXT"
  )
  private String lastName;

  @Column(
          name = "name",
          columnDefinition = "TEXT"
  )
  private String name;

  @Column(
          name = "role",
          nullable = false,
          columnDefinition = "TEXT")
  private String role;

  public Employee(String firstName, String lastName, String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
  }

  public Employee() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return (this.name != null) ? this.name : this.firstName + " " + this.lastName;
  }

  public void setName(String name) {
    String[] nameParts = name.split(" ");
    this.firstName = nameParts[0];
    this.lastName = nameParts[1];
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Employee employee = (Employee) o;
    return Objects.equals(id, employee.id) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(role, employee.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, role);
  }

  @Override
  public String toString() {
    return "Employee{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", role='" + role + '\'' +
            '}';
  }
}
