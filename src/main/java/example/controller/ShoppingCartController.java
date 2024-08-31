package example.controller;

import example.dto.cartItem.RequestCartItemDto;
import example.dto.cartItem.RequestUpdateCartItemDto;
import example.dto.shoppingCart.ShoppingRespondCartDto;
import example.model.User;
import example.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Tag(name = "Shopping Cart Management", description = "Endpoints for managing shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Add item to cart")
    public ShoppingRespondCartDto  addItemToShoppingCart(@RequestBody @Valid RequestCartItemDto cartItemDto,
                                             Authentication authentication) {
        return shoppingCartService.addItemToCart(cartItemDto, getUserId(authentication));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart", description = "Get logged in user shopping cart")
    public ShoppingRespondCartDto getCurrentUserShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(getUserId(authentication));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update shopping cart", description = "Update quantity of a book in the shopping cart")
    public ShoppingRespondCartDto updateShoppingCart(@RequestBody @Valid RequestUpdateCartItemDto cartItemDto,
                                                   @Positive @PathVariable Long cartItemId,
                                                     Authentication authentication) {
        return shoppingCartService.updateCartItem(cartItemDto, cartItemId, getUserId(authentication));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Delete item", description = "Delete book from shopping cart")
    public void deleteItemFromShoppingCart(@PathVariable @Positive Long cartItemId, Authentication authentication) {
        shoppingCartService.deleteCartItem(cartItemId, getUserId(authentication));
    }

    private Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
