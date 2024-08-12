package example.dto.order;

import example.model.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateRequestDto {
    @NotBlank
    private Order.Status status;
}
