package com.ws101.tomacas.EcommerceApi.repository;

import com.ws101.tomacas.EcommerceApi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Category} entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations.
 * Includes a custom finder method to look up categories by name,
 * which is useful when associating products with their categories.</p>
 *
 * @author Jules Ian C. Tomacas
 * @author Jovan P. Atencio
 * @see Category
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name (case-insensitive).
     *
     * @param name the category name to search for
     * @return an Optional containing the category if found, or empty
     */
    Optional<Category> findByNameIgnoreCase(String name);
}
