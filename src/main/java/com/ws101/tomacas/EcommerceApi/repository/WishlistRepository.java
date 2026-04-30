package com.ws101.tomacas.EcommerceApi.repository;

import com.ws101.tomacas.EcommerceApi.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link WishlistItem} entities.
 *
 * @author Jules Ian C. Tomacas
 */
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    /** Finds all wishlist items for a given user. */
    List<WishlistItem> findByUserId(Long userId);

    /** Checks if a specific product is already in the user's wishlist. */
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
}
