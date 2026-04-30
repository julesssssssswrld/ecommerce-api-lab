package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.model.Product;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.model.WishlistItem;
import com.ws101.tomacas.EcommerceApi.repository.ProductRepository;
import com.ws101.tomacas.EcommerceApi.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for wishlist operations.
 *
 * @author Jules Ian C. Tomacas
 */
@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    /** Returns all wishlist items for the given user. */
    public List<WishlistItem> getWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId());
    }

    /** Adds a product to the user's wishlist. Ignores duplicates. */
    public WishlistItem addItem(User user, Long productId) {
        // Check if already wishlisted
        if (wishlistRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new IllegalArgumentException("Product is already in your wishlist.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product with ID " + productId + " was not found."));

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        return wishlistRepository.save(item);
    }

    /** Removes a wishlist item by its ID. */
    public void removeItem(User user, Long itemId) {
        WishlistItem item = wishlistRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Wishlist item with ID " + itemId + " was not found."));
        // Verify ownership
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only remove items from your own wishlist.");
        }
        wishlistRepository.delete(item);
    }
}
