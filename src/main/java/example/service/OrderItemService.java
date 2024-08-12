package example.service;

import example.dto.orderItem.OrderItemRespondDto;
import example.model.CartItem;
import example.model.Order;
import example.model.OrderItem;
import java.util.Set;

public interface OrderItemService {
    Set<OrderItem> createOrderItem(Order order, Set<CartItem> itemSet);

    Set<OrderItemRespondDto> getAllOrderItems(Long id);

    OrderItemRespondDto searchItem(Long orderId, Long itemId);
}
