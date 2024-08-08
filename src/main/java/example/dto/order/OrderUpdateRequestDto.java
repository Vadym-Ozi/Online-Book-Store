package example.dto.order;

import example.model.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateRequestDto {
    private Order.Status status;
}
