package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.model.Product;
import com.ws101.tomacas.EcommerceApi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * REST controller for managing products in the e-commerce catalog.
 *
 * Exposes CRUD endpoints under the {@code /api/v1/products} base
 * path. Acts as the bridge between HTTP requests and the
 * {@link ProductService} business logic layer. This controller
 * only handles request mapping and input validation — all
 * processing is delegated to the service layer.
 *
 * @author Jovan P. Atencio
 * @author Jules Ian C. Tomacas
 * @see ProductService
 * @see Product
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructs a new ProductController with the given service.
     *
     * @param productService the service handling product business logic
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products in the catalog.
     *
     * @return a 200 OK response containing a list of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a single product by its unique ID.
     *
     * @param id the unique identifier of the product
     * @return a 200 OK response with the product data
     * @throws NoSuchElementException if no product with the given ID exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product with ID " + id + " was not found."));
        return ResponseEntity.ok(product);
    }

    /**
     * Filters products based on a specified criteria type and value.
     *
     * <p>Supported filter types:</p>
     * <ul>
     *   <li>{@code category} — matches products by category name</li>
     *   <li>{@code name} — partial match on product name</li>
     *   <li>{@code price} — returns products at or below the given price</li>
     * </ul>
     *
     * @param filterType the criteria to filter by (category, name, or price)
     * @param filterValue the value to match against
     * @return a 200 OK response with the filtered list of products
     * @throws IllegalArgumentException if filterType or filterValue is invalid
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam String filterType,
            @RequestParam String filterValue) {

        // validate that filter parameters are not blank
        if (filterType == null || filterType.isBlank()) {
            throw new IllegalArgumentException("filterType parameter is required.");
        }
        if (filterValue == null || filterValue.isBlank()) {
            throw new IllegalArgumentException("filterValue parameter is required.");
        }

        List<Product> filtered = productService.filterProducts(filterType, filterValue);
        return ResponseEntity.ok(filtered);
    }

    /**
     * Creates a new product in the catalog.
     *
     * Validates the incoming product data before delegating
     * creation to the service layer. Returns 201 Created on success.
     *
     * @param product the product data from the request body
     * @return a 201 Created response with the newly created product
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        validateProduct(product);
        Product created = productService.createProduct(product);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Replaces an existing product entirely (PUT semantics).
     *
     * The entire product object is replaced with the new data.
     * All fields must be provided in the request body.
     *
     * @param id the ID of the product to replace
     * @param product the new product data
     * @return a 200 OK response with the updated product
     * @throws NoSuchElementException if no product with the given ID exists
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        validateProduct(product);
        Product updated = productService.updateProduct(id, product)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product with ID " + id + " was not found."));
        return ResponseEntity.ok(updated);
    }

    /**
     * Partially updates an existing product (PATCH semantics).
     *
     * Only the fields included in the request body will be updated.
     * Fields that are null or have default values are ignored.
     *
     * @param id the ID of the product to patch
     * @param patch a partial product object with fields to update
     * @return a 200 OK response with the patched product
     * @throws NoSuchElementException if no product with the given ID exists
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Product> patchProduct(
            @PathVariable Long id,
            @RequestBody Product patch) {

        validatePatchProduct(patch);
        Product patched = productService.patchProduct(id, patch)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product with ID " + id + " was not found."));
        return ResponseEntity.ok(patched);
    }

    /**
     * Removes a product from the catalog.
     *
     * Returns 204 No Content on successful deletion.
     *
     * @param id the ID of the product to delete
     * @return a 204 No Content response if deletion was successful
     * @throws NoSuchElementException if no product with the given ID exists
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            throw new NoSuchElementException(
                    "Product with ID " + id + " was not found.");
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Validates product fields for POST and PUT requests.
     *
     * Checks that required fields are present and that numeric
     * values are within acceptable ranges.
     *
     * @param product the product to validate
     * @throws IllegalArgumentException if any validation rule is violated
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().length() < 2) {
            throw new IllegalArgumentException(
                    "Product name is required and must be at least 2 characters long.");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException(
                    "Product price must be a positive number.");
        }
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Product category is required.");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be non-negative.");
        }
    }

    /**
     * Validates only the fields present in a PATCH request.
     *
     * Unlike full validation, this method only checks fields that
     * were actually provided (non-null). Missing fields are left
     * unchanged on the existing product.
     *
     * @param patch the partial product object to validate
     * @throws IllegalArgumentException if any provided field has an invalid value
     */
    private void validatePatchProduct(Product patch) {
        if (patch.getName() != null && patch.getName().trim().length() < 2) {
            throw new IllegalArgumentException(
                    "Product name must be at least 2 characters long.");
        }
        if (patch.getPrice() != null && patch.getPrice() <= 0) {
            throw new IllegalArgumentException(
                    "Product price must be a positive number.");
        }
        if (patch.getCategory() != null && patch.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Product category cannot be blank.");
        }
        if (patch.getStockQuantity() != null && patch.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be non-negative.");
        }
    }
}
