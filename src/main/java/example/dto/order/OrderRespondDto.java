package example.dto.order;

import example.dto.orderItem.OrderItemRespondDto;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class OrderRespondDto {
    private Set<OrderItemRespondDto> orders;
}
