package example.service.impl;

import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.exception.EntityNotFoundException;
import example.mapper.BookMapper;
import example.mapper.CartItemMapper;
import example.mapper.ShoppingCartMapper;
import example.model.Book;
import example.model.CartItem;
import example.model.ShoppingCart;
import example.model.User;
import example.repository.book.BookRepository;
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
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ShoppingRespondCartDto  addItemToCart(RequestCartItemDto cartItemDto, Long userId) {
        Book book = bookRepository.findById(cartItemDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find book with id: " + cartItemDto.getBookId())
        );
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        shoppingCart.getCartItems().stream().filter(b -> b.getId().equals(cartItemDto.getBookId()))
                .findAny()
                .ifPresentOrElse(
                        cartItem -> cartItem.setQuantity(cartItemDto.getQuantity()),
                        () -> new CartItem(book, cartItemDto.getQuantity())
                );

        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toRespondDto(shoppingCart);
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
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        cartItem.setQuantity(cartItemDto.quantity());
        cartItemRepository.save(cartItem);
        shoppingCart.setCartItems(Set.of(cartItem));
        return shoppingCartMapper.toRespondDto(shoppingCart);
    }

    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        cartItemRepository.delete(cartItem);
    }
}
