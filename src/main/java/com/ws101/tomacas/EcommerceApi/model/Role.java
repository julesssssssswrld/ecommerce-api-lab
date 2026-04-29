package com.ws101.tomacas.EcommerceApi.model;

/**
 * Enumeration of user roles in the e-commerce system.
 *
 * <p>Used by Spring Security for role-based access control.
 * The ROLE_ prefix is automatically added by Spring Security's
 * {@code hasRole()} method during authorization checks.</p>
 *
 * @author Jules Ian C. Tomacas
 */
public enum Role {
    /** Standard user who can browse and place orders. */
    USER,

    /** Seller who can manage their own products. */
    SELLER,

    /** Administrator with full system access. */
    ADMIN
}
