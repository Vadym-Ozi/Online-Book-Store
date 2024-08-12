package example.dto.order;

import example.dto.orderItem.OrderItemRespondDto;
import example.model.Order;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemRespondDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
