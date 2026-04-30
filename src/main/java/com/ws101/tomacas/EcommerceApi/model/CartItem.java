package com.ws101.tomacas.EcommerceApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity representing a single item in a shopping cart.
 *
 * <p>Links a {@link Product} to a {@link Cart} with a quantity.
 * The product details (name, price, image) are included in the
 * JSON response so the frontend can render the cart without
 * additional API calls.</p>
 *
 * @author Jules Ian C. Tomacas
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Back-reference to the parent cart. Hidden from JSON to prevent circular serialization. */
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    /** The product being added to the cart. */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** How many units of this product are in the cart. */
    @Column(nullable = false)
    private Integer quantity;

    public CartItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
