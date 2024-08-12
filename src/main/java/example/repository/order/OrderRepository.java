package example.repository.order;

import example.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "orderItems"})
    List<Order>  findAllByUserEmail(String email, Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);
}
