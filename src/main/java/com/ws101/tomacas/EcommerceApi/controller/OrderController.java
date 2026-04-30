package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.dto.CreateOrderDto;
import com.ws101.tomacas.EcommerceApi.model.Order;
import com.ws101.tomacas.EcommerceApi.model.OrderItem;
import com.ws101.tomacas.EcommerceApi.model.Product;
import com.ws101.tomacas.EcommerceApi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
     * <p>Validates the incoming DTO using Bean Validation, then
     * converts it to Order and OrderItem entities before
     * delegating to the service layer.</p>
     *
     * @param dto the validated order creation data
     * @return a 201 Created response with the new order
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderDto dto) {
        Order order = new Order();
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerEmail(dto.getCustomerEmail());

        List<OrderItem> items = new ArrayList<>();
        for (CreateOrderDto.OrderItemDto itemDto : dto.getItems()) {
            OrderItem item = new OrderItem();
            item.setQuantity(itemDto.getQuantity());

            // Set the product reference (service will resolve the full entity)
            Product productRef = new Product();
            productRef.setId(itemDto.getProductId());
            item.setProduct(productRef);

            items.add(item);
        }
        order.setOrderItems(items);

        Order created = orderService.createOrder(order);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
