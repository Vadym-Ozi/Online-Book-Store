package example.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequestDto {
    @Pattern(regexp = "^[A-Z][a-z]*(\\s[a-z]+)?$")
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    @Size(max = 200)
    private String description;
}
