package com.ws101.tomacas.EcommerceApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity representing a product in a user's wishlist.
 *
 * <p>A simple many-to-one link between a user and a product.
 * No quantity is tracked — a product is either wishlisted or not.</p>
 *
 * @author Jules Ian C. Tomacas
 */
@Entity
@Table(name = "wishlist_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who wishlisted this product. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /** The wishlisted product. */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public WishlistItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
