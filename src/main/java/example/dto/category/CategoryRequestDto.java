package example.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {
    @NotBlank
    @Pattern(regexp = "^[A-Z][a-z]*$")
    @Size(min = 3, max = 20)
    private String name;
    @Size(max = 200)
    private String description;
}
