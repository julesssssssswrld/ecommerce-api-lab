package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.model.Order;
import com.ws101.tomacas.EcommerceApi.model.OrderItem;
import com.ws101.tomacas.EcommerceApi.model.Product;
import com.ws101.tomacas.EcommerceApi.repository.OrderRepository;
import com.ws101.tomacas.EcommerceApi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for order-related operations.
 *
 * <p>Handles order creation, retrieval, and business logic
 * such as calculating totals and validating product stock.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see Order
 * @see OrderRepository
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Constructs the service with required repositories.
     *
     * @param orderRepository   the JPA repository for orders
     * @param productRepository the JPA repository for product lookups
     */
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return a list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Finds a single order by its ID.
     *
     * @param id the order ID
     * @return the order
     * @throws NoSuchElementException if the order does not exist
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Order with ID " + id + " was not found."));
    }

    /**
     * Creates a new order with the given items.
     *
     * <p>Validates that each referenced product exists, calculates
     * the price at purchase for each item, and computes the total
     * order amount automatically.</p>
     *
     * @param order the order data including items
     * @return the saved order with generated ID
     */
    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());

        double total = 0.0;
        for (OrderItem item : order.getOrderItems()) {
            // Link item back to the order
            item.setOrder(order);

            // Look up the actual product to get the current price
            if (item.getProduct() != null && item.getProduct().getId() != null) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new NoSuchElementException(
                                "Product with ID " + item.getProduct().getId() + " not found."));
                item.setPriceAtPurchase(product.getPrice());
                item.setProduct(product);
            }

            total += item.getPriceAtPurchase() * item.getQuantity();
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }
}
