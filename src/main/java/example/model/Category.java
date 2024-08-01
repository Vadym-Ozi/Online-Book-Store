package example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Table(name = "categories")
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_Deleted=false")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @Column(nullable = false)
    private boolean isDeleted;
}
