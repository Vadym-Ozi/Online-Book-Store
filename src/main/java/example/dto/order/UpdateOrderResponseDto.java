package example.dto.order;

import example.model.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderResponseDto {
    private Order.Status status;
}
