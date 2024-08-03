package example.service.impl;

import example.dto.book.BookDto;
import example.dto.cartItem.CartItemDto;
import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;
import example.exception.EntityNotFoundException;
import example.mapper.BookMapper;
import example.mapper.CartItemMapper;
import example.model.CartItem;
import example.model.ShoppingCart;
import example.repository.cartItem.CartItemRepository;
import example.repository.shoppingCart.ShoppingCartRepository;
import example.service.BookService;
import example.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final CartItemMapper cartItemMapper;
    private final BookMapper bookMapper;

    @Override
    public CartItemDto addItemToCart(RequestCartItemDto cartItemDto) {
        CartItem cartItem = cartItemMapper.toEntity(cartItemDto);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(username);
        BookDto bookDto = bookService.getById(cartItemDto.getBookId());
        cartItem.setBook(bookMapper.toEntity(bookDto));
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(RequestUpdateCartItemDto cartItemDto, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find cart item with id: " + cartItemId)
        );
        cartItem.setQuantity(cartItemDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
