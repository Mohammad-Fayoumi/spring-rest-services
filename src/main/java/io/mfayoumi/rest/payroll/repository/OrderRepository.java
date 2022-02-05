package io.mfayoumi.rest.payroll.repository;

import io.mfayoumi.rest.payroll.entity.Order;
import io.mfayoumi.rest.payroll.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @Query("select o from Order o where o.status = ?1")
  List<Order> findByStatus(Status status);
}
