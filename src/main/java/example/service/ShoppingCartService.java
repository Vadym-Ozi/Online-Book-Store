package example.service;

import example.dto.cartItem.CartItemDto;
import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.model.User;

public interface ShoppingCartService {
    ShoppingRespondCartDto getShoppingCart(Long userId);

    void createShoppingCart(User user);

    CartItemDto addItemToCart(RequestCartItemDto cartItemDto, Long userId);

    ShoppingRespondCartDto updateCartItem(RequestUpdateCartItemDto cartItemDto,
                                          Long cartItemId,
                                          Long userId);

    void deleteCart(Long cartItemId, Long userId);
}
