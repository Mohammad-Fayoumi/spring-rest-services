package io.mfayoumi.rest.payroll;

import io.mfayoumi.rest.payroll.controller.OrderController;
import io.mfayoumi.rest.payroll.entity.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

  @Override
  public EntityModel<Order> toModel(Order order) {

    // Unconditional links to single-item resource and aggregate root.
    EntityModel<Order> orderModel = EntityModel.of(order,
            linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
            linkTo(methodOn(OrderController.class).all()).withRel("orders"));

    // Conditional links based on state of the order.
    switch (order.getStatus()) {
      case IN_PROGRESS:
        orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
        orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        break;
      case CANCELLED:
        orderModel.add(linkTo(methodOn(OrderController.class).canceledOrders()).withRel("canceled orders"));
        break;
      case COMPLETED:
        orderModel.add(linkTo(methodOn(OrderController.class).completedOrders()).withRel("completed orders"));
    }

    return orderModel;
  }
}