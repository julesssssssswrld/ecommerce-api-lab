package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.model.Product;
import com.ws101.tomacas.EcommerceApi.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for product-related operations.
 *
 * <p>Provides business logic for CRUD operations, filtering, and
 * searching of products. Delegates all data access to the
 * {@link ProductRepository}, which persists data to a MySQL
 * database via Spring Data JPA.</p>
 *
 * <p>On application startup, sample data is conditionally seeded
 * into the database if no products exist yet. This ensures that
 * the catalog is pre-populated for demonstration purposes without
 * duplicating data on subsequent restarts.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see Product
 * @see ProductRepository
 */
@Service
public class ProductService {

    /** JPA repository for product data access. */
    private final ProductRepository productRepository;

    /**
     * Constructs the service with the injected repository.
     *
     * @param productRepository the JPA repository for products
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Seeds the database with sample product data if the table is empty.
     *
     * <p>Called automatically after dependency injection is complete.
     * Only inserts data if no products currently exist in the database,
     * preventing duplicate entries on application restarts.</p>
     */
    @PostConstruct
    public void initializeSampleData() {
        if (productRepository.count() > 0) {
            return; // Database already has data; skip seeding
        }

        productRepository.save(new Product(null,
                "Wireless Bluetooth Headphones",
                "Over-ear noise-cancelling headphones with 30-hour battery life.",
                2499.00, "Electronics", 45,
                "https://placehold.co/400x400?text=Headphones", null));

        productRepository.save(new Product(null,
                "Mechanical Keyboard",
                "RGB mechanical keyboard with Cherry MX Blue switches.",
                3899.00, "Electronics", 30,
                "https://placehold.co/400x400?text=Keyboard", null));

        productRepository.save(new Product(null,
                "Running Shoes",
                "Lightweight breathable running shoes with cushioned sole.",
                2199.00, "Footwear", 60,
                "https://placehold.co/400x400?text=Shoes", null));

        productRepository.save(new Product(null,
                "Stainless Steel Water Bottle",
                "Double-walled insulated bottle, keeps drinks cold for 24 hours.",
                899.00, "Home & Kitchen", 120,
                "https://placehold.co/400x400?text=Bottle", null));

        productRepository.save(new Product(null,
                "Laptop Stand",
                "Adjustable aluminum laptop stand with ventilation holes.",
                1599.00, "Electronics", 55,
                "https://placehold.co/400x400?text=Stand", null));

        productRepository.save(new Product(null,
                "Cotton T-Shirt",
                "Premium cotton crew-neck t-shirt available in multiple colors.",
                599.00, "Clothing", 200,
                "https://placehold.co/400x400?text=TShirt", null));

        productRepository.save(new Product(null,
                "Backpack",
                "Water-resistant 25L backpack with padded laptop compartment.",
                1899.00, "Bags & Accessories", 75,
                "https://placehold.co/400x400?text=Backpack", null));

        productRepository.save(new Product(null,
                "Desk Lamp",
                "LED desk lamp with adjustable brightness and color temperature.",
                1299.00, "Home & Kitchen", 40,
                "https://placehold.co/400x400?text=Lamp", null));

        productRepository.save(new Product(null,
                "Yoga Mat",
                "Non-slip 6mm thick yoga mat with carrying strap.",
                749.00, "Fitness", 90,
                "https://placehold.co/400x400?text=YogaMat", null));

        productRepository.save(new Product(null,
                "Wireless Mouse",
                "Ergonomic wireless mouse with adjustable DPI up to 4000.",
                1199.00, "Electronics", 65,
                "https://placehold.co/400x400?text=Mouse", null));

        // Original proof-of-concept products from the frontend
        productRepository.save(new Product(null,
                "Apple Airpods Pro 2",
                "Premium noise cancelling earbuds. Sounds really good trust me bro.",
                10990.00, "Electronics", 25,
                "https://placehold.co/400x400?text=Airpods", null));

        productRepository.save(new Product(null,
                "Apple Watch (Cherry Ultra Edition)",
                "The ultimate smartwatch for the adventurous. Cherry-themed design.",
                69420.00, "Electronics", 10,
                "https://placehold.co/400x400?text=AppleWatch", null));

        productRepository.save(new Product(null,
                "Phone Case",
                "Durable protective phone case with sleek matte finish.",
                149.00, "Accessories", 300,
                "https://placehold.co/400x400?text=PhoneCase", null));
    }

    /**
     * Retrieves all products from the database.
     *
     * @return a {@code List<Product>} containing every product.
     *         Returns an empty list if no products exist.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a single product by its unique ID.
     *
     * @param id the unique identifier of the product to find
     * @return an {@link Optional} containing the product if found,
     *         or {@code Optional.empty()} if no product matches
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Creates a new product and persists it to the database.
     *
     * <p>The product's ID is set to null before saving so that
     * the database auto-generates a unique identifier.</p>
     *
     * @param product the product data to create
     * @return the newly created product with its generated ID
     */
    public Product createProduct(Product product) {
        product.setId(null); // Let the database assign the ID
        return productRepository.save(product);
    }

    /**
     * Replaces an existing product entirely (PUT semantics).
     *
     * <p>Finds the product by ID and replaces all of its fields
     * with the values from the updated product object.</p>
     *
     * @param id the ID of the product to replace
     * @param updatedProduct the new product data
     * @return an {@link Optional} containing the updated product,
     *         or empty if no product with the given ID exists
     */
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            updatedProduct.setId(id);
            return productRepository.save(updatedProduct);
        });
    }

    /**
     * Partially updates an existing product (PATCH semantics).
     *
     * <p>Only non-null fields from the patch object are applied to the
     * existing product. Fields set to {@code null} are left unchanged.</p>
     *
     * @param id the ID of the product to patch
     * @param patch a product object containing only the fields to update
     * @return an {@link Optional} containing the patched product,
     *         or empty if no product with the given ID exists
     */
    public Optional<Product> patchProduct(Long id, Product patch) {
        return productRepository.findById(id).map(existing -> {
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
            return productRepository.save(existing);
        });
    }

    /**
     * Removes a product from the database by its ID.
     *
     * @param id the unique identifier of the product to remove
     * @return {@code true} if a product was found and removed,
     *         {@code false} if no product with the given ID exists
     */
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Filters products by their category (case-insensitive).
     *
     * @param category the category name to filter by
     * @return a list of products matching the given category
     */
    public List<Product> filterByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    /**
     * Filters products whose name contains the given keyword
     * (case-insensitive partial match).
     *
     * @param name the keyword to search for in product names
     * @return a list of products whose name contains the keyword
     */
    public List<Product> filterByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Filters products by a maximum price threshold.
     *
     * @param maxPrice the maximum price threshold (inclusive)
     * @return a list of products priced at or below maxPrice
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public List<Product> filterByPrice(double maxPrice) {
        if (maxPrice < 0) {
            throw new IllegalArgumentException("Max price cannot be negative.");
        }
        return productRepository.findByPriceLessThanEqual(maxPrice);
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
