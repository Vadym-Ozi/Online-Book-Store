package example.service;

import example.dto.order.OrderDto;
import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.dto.order.OrderUpdateRequestDto;
import example.dto.order.UpdateOrderResponseDto;
import example.dto.orderItem.OrderItemRespondDto;
import example.model.CartItem;
import example.model.Order;
import example.model.OrderItem;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

public interface OrderService {
    OrderRespondDto createOrder(OrderRequestDto requestDto, Long userId);

    List<OrderDto> getAllOrders(Pageable pageable, Long userId);

    UpdateOrderResponseDto updateOrderStatus(Long id, OrderUpdateRequestDto status);

    Set<OrderItem> createOrderItem(Order order, Set<CartItem> itemSet);

    Set<OrderItemRespondDto> getAllOrderItems(Long id);

    OrderItemRespondDto searchItem(Long orderId, Long itemId);
}
