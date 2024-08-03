package example.service;

import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.model.User;

public interface ShoppingCartService {
    ShoppingRespondCartDto getShoppingCart();

    void createShoppingCart(User user);
}
