package com.ws101.tomacas.EcommerceApi.repository;

import com.ws101.tomacas.EcommerceApi.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for {@link Cart} entities.
 *
 * @author Jules Ian C. Tomacas
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /** Finds a cart by its owner's user ID. */
    Optional<Cart> findByUserId(Long userId);
}
