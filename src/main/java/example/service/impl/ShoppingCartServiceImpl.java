package example.service.impl;

import example.dto.book.BookDto;
import example.dto.cartItem.CartItemDto;
import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.exception.EntityNotFoundException;
import example.mapper.BookMapper;
import example.mapper.CartItemMapper;
import example.mapper.ShoppingCartMapper;
import example.model.CartItem;
import example.model.ShoppingCart;
import example.model.User;
import example.repository.cartItem.CartItemRepository;
import example.repository.shoppingCart.ShoppingCartRepository;
import example.service.BookService;
import example.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final BookMapper bookMapper;

    @Override
    public CartItemDto addItemToCart(RequestCartItemDto cartItemDto, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        CartItem cartItem = cartItemMapper.toEntity(cartItemDto);
        BookDto bookDto = bookService.getById(cartItemDto.getBookId());
        cartItem.setBook(bookMapper.toEntity(bookDto));
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public ShoppingRespondCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        return shoppingCartMapper.toRespondDto(shoppingCart);
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingRespondCartDto updateCartItem(RequestUpdateCartItemDto cartItemDto,
                                                 Long cartItemId,
                                                 Long userId) {
        CartItem cartItem = findCartItem(cartItemId, userId);
        cartItem.setQuantity(cartItemDto.quantity());
        cartItemRepository.save(cartItem);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        return shoppingCartMapper.toRespondDto(shoppingCart);
    }

    @Override
    public void deleteCart(Long cartItemId, Long userId) {
        cartItemRepository.delete(findCartItem(cartItemId, userId));
    }

    private CartItem findCartItem(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        return cartItems.stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("User has no cart item with id: " + cartItemId)
                );
    }
}
