package example.service.impl;

import example.dto.order.*;
import example.dto.orderItem.OrderItemRespondDto;
import example.exception.EntityNotFoundException;
import example.mapper.OrderItemMapper;
import example.mapper.OrderMapper;
import example.model.*;
import example.repository.order.OrderRepository;
import example.repository.orderItem.OrderItemRepository;
import example.repository.shoppingCart.ShoppingCartRepository;
import example.repository.user.UserRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderRespondDto createOrder(OrderRequestDto requestDto, Long userId) {
        Order order = orderMapper.toEntity(requestDto);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId)
        );
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(totalPrice);
        order.setOrderDate(LocalDateTime.now().withNano(0));
        orderRepository.save(order);

        Set<OrderItem> orderItemSet = createOrderItem(order, shoppingCart.getCartItems());
        order.setOrderItems(orderItemSet);
        order.setTotal(getTotalPrice(orderItemSet));
        Order save = orderRepository.save(order);

        Set<OrderItemRespondDto> respondDto = orderItemMapper.toDto(orderItemSet);
        OrderRespondDto dto = orderMapper.toRespondDto(save);
        dto.setOrders(respondDto);
        return dto;
    }

    @Override
    public List<OrderDto> getAllOrders(Pageable pageable, Long userId) {
        return orderRepository.findAllByUserId(pageable, userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public UpdateOrderResponseDto updateOrderStatus(Long id, OrderUpdateRequestDto status) {
        Order order =  findOrderById(id);
        order.setStatus(status.getStatus());
        orderRepository.save(order);
        return orderMapper.toUpdateRespondDto(new UpdateOrderResponseDto(), status);
    }
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

    private BigDecimal getTotalPrice(Set<OrderItem> orderItemSet) {
        return orderItemSet
                .stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
