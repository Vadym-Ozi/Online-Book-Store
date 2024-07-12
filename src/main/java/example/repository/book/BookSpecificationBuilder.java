package example.repository.book;

import example.dto.BookSearchParameters;
import example.model.Book;
import example.repository.SpecificationBuilder;
import example.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String authorKeyword = "author";
    private static final String priceKeyword = "price";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(authorKeyword)
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.prices() != null && searchParameters.prices().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(priceKeyword)
                    .getSpecification(searchParameters.prices()));
        }
        return spec;
    }
}
