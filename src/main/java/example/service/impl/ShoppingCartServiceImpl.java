package example.service.impl;

import example.dto.cartItem.RespondCartItemDto;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.mapper.CartItemMapper;
import example.mapper.ShoppingCartMapper;
import example.model.CartItem;
import example.model.ShoppingCart;
import example.model.User;
import example.repository.shoppingCart.ShoppingCartRepository;
import example.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingRespondCartDto getShoppingCart() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(username);
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        Set<RespondCartItemDto> dto = cartItemMapper.toDto(cartItems);
        ShoppingRespondCartDto respondDto = shoppingCartMapper.toRespondDto(shoppingCart);
        respondDto.setCartItems(dto);
        return respondDto;
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
