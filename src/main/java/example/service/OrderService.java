package example.service;

import example.dto.order.OrderDto;
import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;
import example.dto.order.OrderUpdateRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderService {
    OrderRespondDto createOrder(OrderRequestDto requestDto);

    List<OrderDto> getAllOrders(Pageable pageable);

    void updateOrderStatus(Long id, OrderUpdateRequestDto status);
}
