package example.repository.book;

import example.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String authorParameter = "author";

    @Override
    public String getKey() {
        return authorParameter;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(authorParameter)
                .in(Arrays.stream(params).toArray());
    }
}
