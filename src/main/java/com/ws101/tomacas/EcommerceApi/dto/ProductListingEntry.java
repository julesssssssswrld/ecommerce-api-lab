package com.ws101.tomacas.EcommerceApi.dto;

/**
 * A lightweight DTO for product listing views.
 *
 * <p>Returns only the essential fields needed for a catalog page:
 * ID, name, and price. This avoids sending unnecessary data like
 * full descriptions, stock quantities, or image URLs in list views.</p>
 *
 * @author Jules Ian C. Tomacas
 */
public record ProductListingEntry(
        Long prodId,
        String prodName,
        double prodPrice
) {}
