package example.service;

import example.dto.order.OrderRequestDto;
import example.dto.order.OrderRespondDto;

public interface OrderService {
    OrderRespondDto createOrder(OrderRequestDto requestDto);
}
