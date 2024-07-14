package example.repository.book;

import example.model.Book;
import example.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE_PARAMETER = "price";

    @Override
    public String getKey() {
        return PRICE_PARAMETER;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(PRICE_PARAMETER)
                .in(Arrays.stream(params).toArray());
    }
}
