package example.dto.orderItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRespondDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
