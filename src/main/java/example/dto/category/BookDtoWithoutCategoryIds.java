package example.dto.category;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDtoWithoutCategoryIds {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
}
