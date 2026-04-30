package com.ws101.tomacas.EcommerceApi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for updating a cart item's quantity.
 *
 * @author Jules Ian C. Tomacas
 */
public class UpdateCartItemDto {

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    public UpdateCartItemDto() {}

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
