package example.controller;

import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Order management", description = "Endpoints to work with orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create new order",
            description = "Create new order and buy an item")
    OrderRespondDto createOrder(@RequestBody OrderRequestDto requestDto) {
        return orderService.createOrder(requestDto);
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
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    Object updateOrder(@PathVariable @Positive Long id) {
        return null;
    }
}