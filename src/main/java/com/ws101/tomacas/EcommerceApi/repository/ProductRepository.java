package com.ws101.tomacas.EcommerceApi.repository;

import com.ws101.tomacas.EcommerceApi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations
 * (save, findById, findAll, deleteById, etc.) without any manual
 * implementation. Custom query methods are defined using both the
 * method naming convention and JPQL annotations.</p>
 *
 * @author Jules Ian C. Tomacas
 * @author Jovan P. Atencio
 * @see Product
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products belonging to a specific category.
     * Uses Spring Data's method naming convention for automatic
     * query generation. The search is case-insensitive.
     *
     * @param category the category name to search for
     * @return a list of products in the given category
     */
    List<Product> findByCategoryIgnoreCase(String category);

    /**
     * Finds all products whose name contains the given keyword.
     * Uses Spring Data's method naming convention. The search
     * is case-insensitive and performs a partial (LIKE) match.
     *
     * @param name the keyword to search for in product names
     * @return a list of products matching the keyword
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Finds all products with a price at or below the given maximum.
     * Uses Spring Data's method naming convention.
     *
     * @param maxPrice the maximum price threshold (inclusive)
     * @return a list of products priced at or below maxPrice
     */
    List<Product> findByPriceLessThanEqual(Double maxPrice);

    /**
     * Custom JPQL query to find products within a specific price range.
     *
     * <p>This demonstrates the use of {@code @Query} with JPQL syntax.
     * Note that JPQL references entity class names (Product) and field
     * names (price), not database table/column names.</p>
     *
     * @param min the minimum price (inclusive)
     * @param max the maximum price (inclusive)
     * @return a list of products within the price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") Double min, @Param("max") Double max);
}
