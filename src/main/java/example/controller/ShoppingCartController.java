package example.controller;

import example.dto.cartItem.CartItemDto;
import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.service.CartItemService;
import example.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart Management", description = "Endpoints for managing shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Add item to cart")
    public CartItemDto addItemToShoppingCart(@RequestBody @Valid RequestCartItemDto cartItemDto) {
        return cartItemService.addItemToCart(cartItemDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart", description = "Get logged in user shopping cart")
    public ShoppingRespondCartDto getCurrentUserShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update shopping cart", description = "Update quantity of a book in the shopping cart")
    public CartItemDto updateShoppingCart(@RequestBody @Valid RequestUpdateCartItemDto cartItemDto,
                                                   @Positive @PathVariable Long cartItemId) {
        return cartItemService.updateCartItem(cartItemDto, cartItemId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Delete item", description = "Delete book from shopping cart")
    public void deleteItemFromShoppingCart(@PathVariable @Positive Long cartItemId) {
        cartItemService.deleteCart(cartItemId);
    }
}
