package com.ws101.tomacas.EcommerceApi.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for partially updating a product (PATCH).
 *
 * <p>All fields are optional (nullable). Validation only applies
 * to fields that are actually provided in the request body.</p>
 *
 * @author Jules Ian C. Tomacas
 */
public class UpdateProductDto {

    /** Product name — if provided, must be 2-100 characters. */
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    /** Product description — if provided, max 500 characters. */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /** Product price — if provided, must be positive. */
    @Positive(message = "Product price must be a positive number")
    private Double price;

    /** Product category — if provided, cannot be blank. */
    private String category;

    /** Stock quantity — if provided, must be zero or positive. */
    @PositiveOrZero(message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    /** Image URL — optional. */
    private String imageUrl;

    public UpdateProductDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
