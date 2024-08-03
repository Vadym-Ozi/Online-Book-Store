package example.dto.cartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespondCartItemDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
