package example.service;

import example.dto.cartItem.CartItemDto;
import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;

public interface CartItemService {
    CartItemDto addItemToCart(RequestCartItemDto cartItemDto);

    CartItemDto updateCartItem(RequestUpdateCartItemDto cartItemDto, Long cartItemId);

    void deleteCart(Long cartItemId);
}
