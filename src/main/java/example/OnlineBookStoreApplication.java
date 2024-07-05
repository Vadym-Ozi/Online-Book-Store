package example;

import example.model.Book;
import example.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {
                Book book1 = new Book();
                book1.setTitle("Book 1");
                book1.setAuthor("Author 1");
                book1.setIsbn("ISBN 1");
                book1.setPrice(BigDecimal.valueOf(500));
                book1.setDescription("Book about mafia");
                book1.setCoverImage("smile");
                bookService.save(book1);
                bookService.findAll();
            }
        };
    }
}
