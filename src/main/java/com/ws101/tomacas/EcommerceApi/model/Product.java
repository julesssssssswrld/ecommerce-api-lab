package com.ws101.tomacas.EcommerceApi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing an e-commerce product.
 *
 * This class defines the core product data structure used throughout
 * the application. Each product has a unique identifier, descriptive
 * fields, pricing info, and inventory tracking.
 *
 * @author Jules Ian C. Tomacas
 * @author Jovan P. Atencio
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Product {

    /** Unique identifier for the product. */
    private Long id;

    /** Display name of the product. */
    private String name;

    /** Detailed description of the product. */
    private String description;

    /** Price of the product in PHP. Must be a positive value. */
    private double price;

    /** Category that the product belongs to (e.g., Electronics, Clothing). */
    private String category;

    /** Number of units currently available in stock. Must be non-negative. */
    private int stockQuantity;

    /** URL pointing to the product image. This field is optional. */
    private String imageUrl;
}
