package example.dto.cartItem;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemDto {
    private Long bookId;
    private int quantity;
}
