package example.dto.cartItem;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCartItemDto {
    @Positive
    private Long bookId;
    @DecimalMin("0")
    private int quantity;
}
