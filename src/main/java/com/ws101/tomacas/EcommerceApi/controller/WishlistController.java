package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.dto.AddWishlistItemDto;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.model.WishlistItem;
import com.ws101.tomacas.EcommerceApi.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing the authenticated user's wishlist.
 *
 * <p>All endpoints require authentication. The wishlist is a
 * simple list of products the user is interested in, without
 * quantities.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see WishlistService
 */
@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /** Retrieves all items in the user's wishlist. */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WishlistItem>> getWishlist(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(wishlistService.getWishlist(user));
    }

    /** Adds a product to the wishlist. */
    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WishlistItem> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddWishlistItemDto dto) {
        WishlistItem item = wishlistService.addItem(user, dto.getProductId());
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    /** Removes a product from the wishlist. */
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        wishlistService.removeItem(user, itemId);
        return ResponseEntity.noContent().build();
    }
}
