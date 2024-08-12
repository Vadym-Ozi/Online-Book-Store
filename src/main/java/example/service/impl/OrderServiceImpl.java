package example.service.impl;

import example.dto.order.OrderDto;
import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.dto.order.OrderUpdateRequestDto;
import example.dto.orderItem.OrderItemRespondDto;
import example.exception.EntityNotFoundException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        OrderRespondDto dto = orderMapper.toRespondDto(save);
        dto.setOrders(respondDto);
        return dto;
    }

    @Override
    public List<OrderDto> getAllOrders(Pageable pageable) {
        String email = getUser().getEmail();
        return orderRepository.findAllByUserEmail(email, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public void updateOrderStatus(Long id, OrderUpdateRequestDto status) {
        Order order =  orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find order with id: " + id)
        );
        order.setStatus(status.getStatus());
        orderRepository.save(order);
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
