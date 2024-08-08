package example.service.impl;

import example.model.CartItem;
import example.model.Order;
import example.model.OrderItem;
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
}
