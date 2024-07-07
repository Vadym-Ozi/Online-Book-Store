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
public class Application {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {
                Book book = new Book();
                book.setTitle("Book 1");
                book.setAuthor("Author 1");
                book.setIsbn("ISBN 1");
                book.setPrice(BigDecimal.valueOf(500));
                book.setDescription("Book about mafia");
                book.setCoverImage("smile");
                bookService.save(book);
                bookService.findAll();
            }
        };
    }
}
