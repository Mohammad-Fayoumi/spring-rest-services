package io.mfayoumi.rest.payroll.entity;

import io.mfayoumi.rest.payroll.enums.Status;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "customer_orders")
public class Order {
  @Id
  @SequenceGenerator(
          name = "order_sequence",
          sequenceName = "order_sequence",
          allocationSize = 50
  )
  @Column(name = "id", updatable = false)
  @GeneratedValue(
          strategy = GenerationType.SEQUENCE,
          generator = "order_sequence"
  )
  private Long id;

  @Column(
          name = "description",
          columnDefinition = "TEXT"
  )
  private String description;

  @Column(
          name = "status",
          columnDefinition = "TEXT"
  )
  @Enumerated(EnumType.STRING)
  private Status status;

  public Order(Long id, String description, Status status) {
    this.id = id;
    this.description = description;
    this.status = status;
  }

  public Order() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Order order = (Order) o;
    return Objects.equals(id, order.id) && Objects.equals(description, order.description) && status == order.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, status);
  }

  @Override
  public String toString() {
    return "Order{" +
            "id=" + id +
            ", description='" + description + '\'' +
            ", status=" + status +
            '}';
  }
}
