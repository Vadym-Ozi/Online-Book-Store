package example.dto.cartItem;

import jakarta.validation.constraints.DecimalMin;

public record RequestUpdateCartItemDto(@DecimalMin("0") int quantity) {
}
