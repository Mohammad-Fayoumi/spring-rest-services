package io.mfayoumi.rest.payroll.controller;

import io.mfayoumi.rest.payroll.OrderModelAssembler;
import io.mfayoumi.rest.payroll.enums.Status;
import io.mfayoumi.rest.payroll.entity.Order;
import io.mfayoumi.rest.payroll.exception.OrderNotFoundException;
import io.mfayoumi.rest.payroll.repository.OrderRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
  private final OrderRepository orderRepository;
  private final OrderModelAssembler assembler;

  OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {

    this.orderRepository = orderRepository;
    this.assembler = assembler;
  }

  @GetMapping()
  public CollectionModel<EntityModel<Order>> all() {

    List<EntityModel<Order>> orders = orderRepository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

    return CollectionModel.of(orders,
            linkTo(methodOn(OrderController.class).all()).withSelfRel());
  }

  @GetMapping("/canceled")
  public CollectionModel<EntityModel<Order>> canceledOrders() {
    List<EntityModel<Order>> canceledOrders = this.orderRepository.findByStatus(Status.CANCELLED)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

    return CollectionModel.of(canceledOrders,
            linkTo(methodOn(OrderController.class).canceledOrders()).withSelfRel());
  }

  @GetMapping("/completed")
  public CollectionModel<EntityModel<Order>> completedOrders() {
    List<EntityModel<Order>> completedOrders = this.orderRepository.findByStatus(Status.COMPLETED)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

    return CollectionModel.of(completedOrders,
            linkTo(methodOn(OrderController.class).completedOrders()).withSelfRel());
  }

  @GetMapping("/{id}")
  public EntityModel<Order> getOrderById(@PathVariable Long id) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

    return assembler.toModel(order);
  }

  @PostMapping()
  ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {

    order.setStatus(Status.IN_PROGRESS);
    Order newOrder = this.orderRepository.save(order);

    return ResponseEntity
            .created(linkTo(methodOn(OrderController.class).getOrderById(newOrder.getId())).toUri())
            .body(assembler.toModel(newOrder));
  }

  @DeleteMapping("/{id}/cancel")
  public ResponseEntity<?> cancel(@PathVariable Long id) {
    Order order = this.orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.CANCELLED);
      return ResponseEntity.ok(this.assembler.toModel(this.orderRepository.save(order)));
    }

    return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .header(HttpHeaders.CONTENT_TYPE,
                    MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(Problem.create()
                    .withTitle("Method Not Allowed")
                    .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
  }

  @PutMapping("/{id}/complete")
  public ResponseEntity<?> complete(@PathVariable Long id) {
    Order order = this.orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.COMPLETED);
      return ResponseEntity.ok(this.assembler.toModel(this.orderRepository.save(order)));
    }

    return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .header(HttpHeaders.CONTENT_TYPE,
                    MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(Problem.create()
                    .withTitle("Method Not Allowed")
                    .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
  }
}
