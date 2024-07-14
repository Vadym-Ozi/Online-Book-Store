package example.repository.book;

import example.model.Book;
import example.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String AUTHOR_PARAMETER = "author";

    @Override
    public String getKey() {
        return AUTHOR_PARAMETER;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(AUTHOR_PARAMETER)
                .in(Arrays.stream(params).toArray());
    }
}
