package example.controller;

import example.dto.order.OrderDto;
import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.dto.order.OrderUpdateRequestDto;
import example.dto.order.UpdateOrderResponseDto;
import example.dto.orderItem.OrderItemRespondDto;
import example.security.AuthenticationService;
import example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/orders")
@Tag(name = "Order management", description = "Endpoints to work with orders")
public class OrderController {
    private final OrderService orderService;
    private final AuthenticationService authenticationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create new order",
            description = "Create new order and buy an item")
    public OrderRespondDto createOrder(@RequestBody OrderRequestDto requestDto,
                                       Authentication authentication) {
        Long userId = authenticationService.getUserId(authentication);
        return orderService.createOrder(requestDto, userId);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all orders",
            description = "Get history of all orders for current user")
    public List<OrderDto> getOrderHistory(@ParameterObject @PageableDefault Pageable pageable,
                                          Authentication authentication) {
        Long userId = authenticationService.getUserId(authentication);
        return orderService.getAllOrders(pageable, userId);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order",
            description = "Update order status for managing order processing workflow")
    public UpdateOrderResponseDto updateOrder(@PathVariable @Positive Long id, @RequestBody OrderUpdateRequestDto status) {
        return orderService.updateOrderStatus(id, status);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all items in order",
            description = "Retrieve all order items for a specific order")
    public Set<OrderItemRespondDto> getAllOrderItems(@PathVariable @Positive Long orderId) {
        return orderService.getAllOrderItems(orderId);
    }

    @GetMapping("/{orderId}/item/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Search for order item by id",
            description = "Retrieve a specific order item within an order id")
    public OrderItemRespondDto searchOrderItem(@PathVariable @Positive Long orderId,
                                        @PathVariable @Positive Long itemId) {
        return orderService.searchItem(orderId, itemId);
    }

}