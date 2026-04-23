package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service class for product-related operations.
 *
 * Provides business logic for CRUD operations, filtering, and
 * searching of products. Uses an in-memory {@code ArrayList} as
 * the data store — no database is involved.
 *
 * <p>Products are initialized with sample data on startup. A
 * thread-safe {@link AtomicLong} counter handles unique ID
 * generation since there is no auto-increment from a database.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see Product
 */
@Service
public class ProductService {

    /** In-memory product storage using ArrayList. */
    private final List<Product> productList = new ArrayList<>();

    /** Thread-safe counter for generating unique product IDs. */
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Initializes the service with sample product data.
     * Called on construction to pre-populate the product catalog
     * with 10 sample items across different categories.
     */
    public ProductService() {
        initializeSampleData();
    }

    /**
     * Populates the product list with 10 sample products
     * for demonstration and testing purposes.
     */
    private void initializeSampleData() {
        productList.add(new Product(idCounter.getAndIncrement(),
                "Wireless Bluetooth Headphones",
                "Over-ear noise-cancelling headphones with 30-hour battery life.",
                2499.00, "Electronics", 45,
                "https://placehold.co/400x400?text=Headphones"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Mechanical Keyboard",
                "RGB mechanical keyboard with Cherry MX Blue switches.",
                3899.00, "Electronics", 30,
                "https://placehold.co/400x400?text=Keyboard"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Running Shoes",
                "Lightweight breathable running shoes with cushioned sole.",
                2199.00, "Footwear", 60,
                "https://placehold.co/400x400?text=Shoes"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Stainless Steel Water Bottle",
                "Double-walled insulated bottle, keeps drinks cold for 24 hours.",
                899.00, "Home & Kitchen", 120,
                "https://placehold.co/400x400?text=Bottle"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Laptop Stand",
                "Adjustable aluminum laptop stand with ventilation holes.",
                1599.00, "Electronics", 55,
                "https://placehold.co/400x400?text=Stand"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Cotton T-Shirt",
                "Premium cotton crew-neck t-shirt available in multiple colors.",
                599.00, "Clothing", 200,
                "https://placehold.co/400x400?text=TShirt"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Backpack",
                "Water-resistant 25L backpack with padded laptop compartment.",
                1899.00, "Bags & Accessories", 75,
                "https://placehold.co/400x400?text=Backpack"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Desk Lamp",
                "LED desk lamp with adjustable brightness and color temperature.",
                1299.00, "Home & Kitchen", 40,
                "https://placehold.co/400x400?text=Lamp"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Yoga Mat",
                "Non-slip 6mm thick yoga mat with carrying strap.",
                749.00, "Fitness", 90,
                "https://placehold.co/400x400?text=YogaMat"));

        productList.add(new Product(idCounter.getAndIncrement(),
                "Wireless Mouse",
                "Ergonomic wireless mouse with adjustable DPI up to 4000.",
                1199.00, "Electronics", 65,
                "https://placehold.co/400x400?text=Mouse"));
    }

    /**
     * Retrieves all products in the catalog.
     *
     * @return a {@code List<Product>} containing every product
     *         currently stored. Returns an empty list if no
     *         products exist.
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    /**
     * Finds a single product by its unique ID.
     *
     * @param id the unique identifier of the product to find
     * @return an {@link Optional} containing the product if found,
     *         or {@code Optional.empty()} if no product matches
     */
    public Optional<Product> getProductById(Long id) {
        return productList.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    /**
     * Creates a new product and adds it to the catalog.
     *
     * The product is assigned a unique ID generated by the
     * internal counter, regardless of any ID value that may
     * have been set on the input object.
     *
     * @param product the product data to create (ID will be overwritten)
     * @return the newly created product with its assigned ID
     */
    public Product createProduct(Product product) {
        product.setId(idCounter.getAndIncrement());
        productList.add(product);
        return product;
    }

    /**
     * Replaces an existing product entirely (PUT semantics).
     *
     * Finds the product by ID and replaces all of its fields
     * with the values from the updated product object.
     *
     * @param id the ID of the product to replace
     * @param updatedProduct the new product data
     * @return an {@link Optional} containing the updated product,
     *         or empty if no product with the given ID exists
     */
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(id)) {
                updatedProduct.setId(id);
                productList.set(i, updatedProduct);
                return Optional.of(updatedProduct);
            }
        }
        return Optional.empty();
    }

    /**
     * Partially updates an existing product (PATCH semantics).
     *
     * Only non-null fields from the patch object are applied to the
     * existing product. Fields set to {@code null} (i.e., not included
     * in the request body) are left unchanged.
     *
     * @param id the ID of the product to patch
     * @param patch a product object containing only the fields to update
     * @return an {@link Optional} containing the patched product,
     *         or empty if no product with the given ID exists
     */
    public Optional<Product> patchProduct(Long id, Product patch) {
        Optional<Product> existingOpt = getProductById(id);

        if (existingOpt.isPresent()) {
            Product existing = existingOpt.get();

            // Only update fields that were actually provided (non-null)
            if (patch.getName() != null) {
                existing.setName(patch.getName());
            }
            if (patch.getDescription() != null) {
                existing.setDescription(patch.getDescription());
            }
            if (patch.getPrice() != null) {
                existing.setPrice(patch.getPrice());
            }
            if (patch.getCategory() != null) {
                existing.setCategory(patch.getCategory());
            }
            if (patch.getStockQuantity() != null) {
                existing.setStockQuantity(patch.getStockQuantity());
            }
            if (patch.getImageUrl() != null) {
                existing.setImageUrl(patch.getImageUrl());
            }
            return Optional.of(existing);
        }
        return Optional.empty();
    }

    /**
     * Removes a product from the catalog by its ID.
     *
     * @param id the unique identifier of the product to remove
     * @return {@code true} if a product was found and removed,
     *         {@code false} if no product with the given ID exists
     */
    public boolean deleteProduct(Long id) {
        return productList.removeIf(product -> product.getId().equals(id));
    }

    /**
     * Filters products by their category (case-insensitive).
     *
     * @param category the category name to filter by
     * @return a list of products matching the given category.
     *         Returns an empty list if no products match.
     */
    public List<Product> filterByCategory(String category) {
        return productList.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Filters products whose name contains the given keyword
     * (case-insensitive partial match).
     *
     * @param name the keyword to search for in product names
     * @return a list of products whose name contains the keyword.
     *         Returns an empty list if no products match.
     */
    public List<Product> filterByName(String name) {
        return productList.stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filters products by a maximum price threshold.
     *
     * Retrieves all products with a price less than or equal
     * to the specified value.
     *
     * @param maxPrice the maximum price threshold (inclusive)
     * @return a list of products priced at or below maxPrice.
     *         Returns an empty list if no products match.
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public List<Product> filterByPrice(double maxPrice) {
        if (maxPrice < 0) {
            throw new IllegalArgumentException("Max price cannot be negative.");
        }
        return productList.stream()
                .filter(p -> p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * General-purpose filter that delegates to specific filter
     * methods based on the filter type provided.
     *
     * <p>Supported filter types:</p>
     * <ul>
     *   <li>{@code "category"} — filters by category name</li>
     *   <li>{@code "name"} — filters by product name (partial match)</li>
     *   <li>{@code "price"} — filters by max price</li>
     * </ul>
     *
     * @param filterType the type of filter to apply
     * @param filterValue the value to filter by
     * @return a list of products matching the filter criteria
     * @throws IllegalArgumentException if filterType is not supported
     *         or if filterValue is invalid for the given type
     */
    public List<Product> filterProducts(String filterType, String filterValue) {
        switch (filterType.toLowerCase()) {
            case "category":
                return filterByCategory(filterValue);
            case "name":
                return filterByName(filterValue);
            case "price":
                try {
                    double maxPrice = Double.parseDouble(filterValue);
                    return filterByPrice(maxPrice);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid price value: '" + filterValue + "'. Must be a valid number.");
                }
            default:
                throw new IllegalArgumentException(
                        "Unsupported filter type: '" + filterType
                                + "'. Supported types: category, name, price");
        }
    }
}
