package com.ws101.tomacas.EcommerceApi.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Data Transfer Object for creating a new order.
 *
 * <p>Validates that customer information and at least one
 * order item are provided before processing.</p>
 *
 * @author Jules Ian C. Tomacas
 */
public class CreateOrderDto {

    /** Customer's full name — required. */
    @NotBlank(message = "Customer name is required")
    private String customerName;

    /** Customer's email — required, must be a valid email format. */
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email address")
    private String customerEmail;

    /** List of order items — required, at least one item. */
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDto> items;

    public CreateOrderDto() {}

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    /**
     * Nested DTO representing a single item in an order.
     */
    public static class OrderItemDto {

        /** Product ID to order. */
        @NotNull(message = "Product ID is required")
        private Long productId;

        /** Quantity — must be at least 1. */
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be a positive number")
        private Integer quantity;

        public OrderItemDto() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
