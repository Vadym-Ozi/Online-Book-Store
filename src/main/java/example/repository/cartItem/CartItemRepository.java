package example.repository.cartItem;

import example.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByIdAndShoppingCartId(Long cartItemId, Long shoppingCartId);
}
