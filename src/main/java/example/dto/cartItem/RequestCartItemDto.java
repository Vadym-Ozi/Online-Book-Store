package example.dto.cartItem;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class RequestCartItemDto {
    @Positive
    @NonNull
    private Long bookId;
    @DecimalMin("0")
    private int quantity;
}
