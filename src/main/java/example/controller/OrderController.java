package example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Order management", description = "Endpoints to work with orders")
public class OrderController {


    //buy a book in the shopping cart
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    Object createOrder(Object o) {
        return null;
    }

    // return history of past purchases
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    Object getOrderHistory(Object o) {
        return null;
    }

    //view the items in my order, so I can check the details of my purchase  to retrieve all OrderItems for a specific order.
    @GetMapping("/{orderId}/item")
    @PreAuthorize("hasRole('USER')")
    Object searchOrderItems(@PathVariable Long id, Object o) {
        return null;
    }

    // to view a specific item in my order, so I can check its details
    //retrieve a specific OrderItem within an order.
    @GetMapping("/{orderId}/item/{id}")
    @PreAuthorize("hasRole('USER')")
    Object searchItem(@PathVariable Long orderId, Object o, Long itemId) {
        return null;
    }

    //to update order status, so I can manage the order processing workflow. to update the status of an order.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    Object updateOrder(@PathVariable @Positive Long id) {
        return null;
    }

}