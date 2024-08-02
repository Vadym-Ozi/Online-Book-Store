package example.dto.category;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    @Pattern(regexp = "^[A-Z][a-z]*$")
    @Size(min = 3, max = 20)
    private String name;
    @Size(max = 200)
    private String description;
}
