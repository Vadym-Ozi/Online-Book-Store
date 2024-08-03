package example.dto.shoppingCart;

import example.dto.cartItem.RespondCartItemDto;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class ShoppingRespondCartDto {
    private Long id;
    private Long userId;
    private Set<RespondCartItemDto> cartItems;
}
