package example.dto.book;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotNull
    @DecimalMin("0")
    private BigDecimal price;
    @NotBlank
    @Pattern(regexp = "^[0-9]{13}$", message = "ISBN must be positive and contains 13 digits")
    private String isbn;
    private String description;
    private String coverImage;
    private List<Long> categoryIds;
}
