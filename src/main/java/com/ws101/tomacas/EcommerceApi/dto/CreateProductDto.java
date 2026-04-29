package com.ws101.tomacas.EcommerceApi.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for creating a new product.
 *
 * <p>Uses Bean Validation annotations to enforce constraints
 * before data reaches the service layer. Spring's {@code @Valid}
 * annotation triggers validation automatically in the controller.</p>
 *
 * @author Jules Ian C. Tomacas
 */
public class CreateProductDto {

    /** Product name — required, 2-100 characters. */
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    /** Product description — optional, max 500 characters. */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /** Product price — required, must be positive. */
    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be a positive number")
    private Double price;

    /** Product category — required. */
    @NotBlank(message = "Product category is required")
    private String category;

    /** Stock quantity — required, must be zero or positive. */
    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    /** Image URL — optional, validated if provided. */
    private String imageUrl;

    public CreateProductDto() {}

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
