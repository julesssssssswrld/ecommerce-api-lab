package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.model.Order;
import com.ws101.tomacas.EcommerceApi.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing customer orders.
 *
 * <p>All order endpoints require authentication. Creating an
 * order requires the user to be logged in, while viewing all
 * orders is restricted to admin users.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see OrderService
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Constructs the controller with the order service.
     *
     * @param orderService the service handling order business logic
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves all orders. Restricted to admin users.
     *
     * @return a 200 OK response with the list of all orders
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Retrieves a single order by ID. Requires authentication.
     *
     * @param id the order ID
     * @return a 200 OK response with the order data
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Creates a new order. Requires the user to be authenticated.
     *
     * @param order the order data from the request body
     * @return a 201 Created response with the new order
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
