package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.model.Cart;
import com.ws101.tomacas.EcommerceApi.model.CartItem;
import com.ws101.tomacas.EcommerceApi.model.Product;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.repository.CartRepository;
import com.ws101.tomacas.EcommerceApi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service class for shopping cart operations.
 *
 * <p>Manages the lifecycle of a user's cart: creating it on
 * first access, adding/removing items, updating quantities,
 * and clearing the cart after checkout.</p>
 *
 * @author Jules Ian C. Tomacas
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Retrieves the user's cart, creating one if it doesn't exist.
     *
     * @param user the authenticated user
     * @return the user's cart
     */
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    /**
     * Adds a product to the cart. If the product is already in the
     * cart, the quantity is increased.
     *
     * @param user      the authenticated user
     * @param productId the ID of the product to add
     * @param quantity  how many units to add
     * @return the updated cart
     */
    public Cart addItem(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product with ID " + productId + " was not found."));

        // If already in cart, just increase quantity
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    /**
     * Removes an item from the cart by its cart item ID.
     *
     * @param user   the authenticated user
     * @param itemId the ID of the cart item to remove
     * @return the updated cart
     */
    public Cart removeItem(User user, Long itemId) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return cartRepository.save(cart);
    }

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param user     the authenticated user
     * @param itemId   the cart item ID
     * @param quantity the new quantity
     * @return the updated cart
     */
    public Cart updateItemQuantity(User user, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Cart item with ID " + itemId + " was not found."))
                .setQuantity(quantity);
        return cartRepository.save(cart);
    }

    /**
     * Removes all items from the user's cart (e.g. after checkout).
     *
     * @param user the authenticated user
     */
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
