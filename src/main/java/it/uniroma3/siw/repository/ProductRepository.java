package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Category;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.model.Users;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Basic queries
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images WHERE p.seller = :seller")
    List<Product> findBySellerWithImages(@Param("seller") Users seller);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images")
    List<Product> findAllWithImages();
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.id = :id")
    Optional<Product> findByIdWithSeller(@Param("id") Long id);
    
    List<Product> findBySeller(Users seller);
    
    // Products by category
    List<Product> findByCategory(Category category);
    
    // Recent products ordered by ID descending
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.seller ORDER BY p.id DESC")
    List<Product> findAllOrderByIdDesc();
    
    // Products with ratings for featured section
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller LEFT JOIN p.ratings")
    List<Product> findAllWithRatings();
    
    // Get ratings for a specific product
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId")
    List<Rating> findRatingsForProduct(@Param("productId") Long productId);
    
    // Get recent ratings for a specific product (ordered by ID desc)
    @Query("SELECT r FROM Rating r LEFT JOIN FETCH r.user WHERE r.product.id = :productId ORDER BY r.id DESC")
    List<Rating> findRecentRatingsForProductOrderByIdDesc(@Param("productId") Long productId);
    
    // Search and filter queries
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:priceMin IS NULL OR p.price >= :priceMin) " +
           "AND (:priceMax IS NULL OR p.price <= :priceMax)")
    List<Product> findProductsWithFilters(@Param("searchTerm") String searchTerm,
                                         @Param("category") Category category,
                                         @Param("priceMin") Double priceMin,
                                         @Param("priceMax") Double priceMax);
    
    // Search with pagination
    @Query("SELECT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:priceMin IS NULL OR p.price >= :priceMin) " +
           "AND (:priceMax IS NULL OR p.price <= :priceMax)")
    Page<Product> findProductsWithFiltersPageable(@Param("searchTerm") String searchTerm,
                                                 @Param("category") Category category,
                                                 @Param("priceMin") Double priceMin,
                                                 @Param("priceMax") Double priceMax,
                                                 Pageable pageable);
}