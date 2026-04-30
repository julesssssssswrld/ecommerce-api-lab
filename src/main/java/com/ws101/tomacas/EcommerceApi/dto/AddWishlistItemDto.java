package com.ws101.tomacas.EcommerceApi.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for adding a product to the wishlist.
 *
 * @author Jules Ian C. Tomacas
 */
public class AddWishlistItemDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    public AddWishlistItemDto() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}
