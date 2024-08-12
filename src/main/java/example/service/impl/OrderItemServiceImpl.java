package example.service.impl;

import example.dto.orderItem.OrderItemRespondDto;
import example.exception.EntityNotFoundException;
import example.mapper.OrderItemMapper;
import example.model.CartItem;
import example.model.Order;
import example.model.OrderItem;
import example.repository.order.OrderRepository;
import example.repository.orderItem.OrderItemRepository;
import example.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Set<OrderItem> createOrderItem(Order order, Set<CartItem> itemSet) {
        Set<OrderItem> orderItemSet = itemSet.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            return orderItem;
        }).collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItemSet);
        return orderItemSet;
    }

    @Override
    public Set<OrderItemRespondDto> getAllOrderItems(Long id) {
        return orderItemMapper.toDto(findOrderById(id).getOrderItems());
    }

    @Override
    public OrderItemRespondDto searchItem(Long orderId, Long itemId) {
        Set<OrderItem> orderItems = findOrderById(orderId).getOrderItems();
        OrderItem orderItem = orderItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can`t find order item with id: " + itemId)
                );
        return orderItemMapper.toDto(orderItem);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find order with id: " + id)
        );
    }
}
