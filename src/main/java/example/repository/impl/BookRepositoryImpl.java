package example.repository.impl;

import example.exception.DataProcessingException;
import example.model.Book;
import example.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    @Autowired
    private final SessionFactory factory;

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can`t add book. " + book);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return book;
    }

    @Override
    public List<Book> getAll() {
        try (Session session = factory.openSession()) {
            return session.createQuery("SELECT u FROM Book u", Book.class).getResultList();
        } catch (RuntimeException e) {
            throw new DataProcessingException("Can`t find all books." + e);
        }
    }

    @Override
    public Optional<Book> getById(Long id) {
        try (Session session = factory.openSession()) {
            Book book = session.get(Book.class, id);
            return Optional.ofNullable(book);
        } catch (RuntimeException e) {
            throw new DataProcessingException("Can`t find book with id: " + id);
        }
    }
}
