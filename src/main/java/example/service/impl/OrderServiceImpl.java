package example.service.impl;

import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.dto.orderItem.OrderItemRespondDto;
import example.mapper.OrderItemMapper;
import example.mapper.OrderMapper;
import example.model.Order;
import example.model.OrderItem;
import example.model.ShoppingCart;
import example.model.User;
import example.repository.order.OrderRepository;
import example.repository.shoppingCart.ShoppingCartRepository;
import example.service.OrderItemService;
import example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemService orderItemService;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderRespondDto createOrder(OrderRequestDto requestDto) {
        Order order = orderMapper.toEntity(requestDto);
        User user = getUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(user.getEmail());
        BigDecimal totalPrice = BigDecimal.ZERO;
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(totalPrice);
        order.setOrderDate(LocalDateTime.now().withNano(0));
        orderRepository.save(order);

        Set<OrderItem> orderItemSet = orderItemService.createOrderItem(order, shoppingCart.getCartItems());
        order.setOrderItems(orderItemSet);
        order.setTotal(getTotalPrice(orderItemSet));
        Order save = orderRepository.save(order);

        Set<OrderItemRespondDto> respondDto = orderItemMapper.toDto(orderItemSet);
        OrderRespondDto dto = orderMapper.toDto(save);
        dto.setOrders(respondDto);
        return dto;

    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private BigDecimal getTotalPrice(Set<OrderItem> orderItemSet) {
        return orderItemSet
                .stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
