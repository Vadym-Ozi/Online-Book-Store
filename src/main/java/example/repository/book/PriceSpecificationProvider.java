package example.repository.book;

import example.model.Book;
import example.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String priceParameter = "price";

    @Override
    public String getKey() {
        return priceParameter;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(priceParameter)
                .in(Arrays.stream(params).toArray());
    }
}
