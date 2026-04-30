package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.dto.AddCartItemDto;
import com.ws101.tomacas.EcommerceApi.dto.UpdateCartItemDto;
import com.ws101.tomacas.EcommerceApi.model.Cart;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the authenticated user's shopping cart.
 *
 * <p>All endpoints require authentication. The cart is automatically
 * created on first access and persisted in the database so it
 * survives across sessions.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see CartService
 */
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves the current user's cart with all items.
     *
     * @param user the authenticated user (injected by Spring Security)
     * @return a 200 OK response with the cart and its items
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getOrCreateCart(user));
    }

    /**
     * Adds a product to the cart. If the product is already present,
     * its quantity is increased.
     *
     * @param user the authenticated user
     * @param dto  the validated add-item request
     * @return a 201 Created response with the updated cart
     */
    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddCartItemDto dto) {
        Cart cart = cartService.addItem(user, dto.getProductId(), dto.getQuantity());
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    /**
     * Updates the quantity of a specific cart item.
     *
     * @param user   the authenticated user
     * @param itemId the cart item ID to update
     * @param dto    the validated quantity update
     * @return a 200 OK response with the updated cart
     */
    @PatchMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> updateItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemDto dto) {
        return ResponseEntity.ok(
                cartService.updateItemQuantity(user, itemId, dto.getQuantity()));
    }

    /**
     * Removes a specific item from the cart.
     *
     * @param user   the authenticated user
     * @param itemId the cart item ID to remove
     * @return a 200 OK response with the updated cart
     */
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(user, itemId));
    }

    /**
     * Clears all items from the cart (e.g. after placing an order).
     *
     * @param user the authenticated user
     * @return a 204 No Content response
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
