package com.ws101.tomacas.EcommerceApi.repository;

import com.ws101.tomacas.EcommerceApi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for {@link Order} entities.
 *
 * <p>Provides standard CRUD operations for orders through
 * Spring Data JPA auto-implementation.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see Order
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
