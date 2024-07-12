package example.repository.book;

import example.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String titleParameter = "title";

    @Override
    public String getKey() {
        return titleParameter;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(titleParameter)
                .in(Arrays.stream(params).toArray());
    }
}
