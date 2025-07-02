package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.model.Users;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images WHERE p.autore = :autore")
    List<Product> findByAutoreWithImages(@Param("autore") Users autore);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images")
    List<Product> findAllWithImages();
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.autore WHERE p.id = :id")
    Optional<Product> findByIdWithAutore(@Param("id") Long id);
    
    List<Product> findByAutore(Users autore);
    
    // Query per ricerca e filtri
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax)")
    List<Product> findProductsWithFilters(@Param("searchTerm") String searchTerm,
                                         @Param("categoria") String categoria,
                                         @Param("prezzoMin") Double prezzoMin,
                                         @Param("prezzoMax") Double prezzoMax);
    
    // Query per ottenere tutte le categorie distinte
    @Query("SELECT DISTINCT p.categoria FROM Product p WHERE p.categoria IS NOT NULL ORDER BY p.categoria")
    List<String> findDistinctCategories();
    
    // Query per ricerca con paginazione
    @Query("SELECT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax)")
    Page<Product> findProductsWithFiltersPageable(@Param("searchTerm") String searchTerm,
                                                 @Param("categoria") String categoria,
                                                 @Param("prezzoMin") Double prezzoMin,
                                                 @Param("prezzoMax") Double prezzoMax,
                                                 Pageable pageable);
    
    // Query per ordinamento specifico
    // Metodi separati per ogni tipo di ordinamento
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "ORDER BY p.nome ASC")
    List<Product> findProductsWithFiltersOrderByNome(@Param("searchTerm") String searchTerm,
                                                     @Param("categoria") String categoria,
                                                     @Param("prezzoMin") Double prezzoMin,
                                                     @Param("prezzoMax") Double prezzoMax);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "ORDER BY p.prezzo ASC")
    List<Product> findProductsWithFiltersOrderByPrezzo(@Param("searchTerm") String searchTerm,
                                                       @Param("categoria") String categoria,
                                                       @Param("prezzoMin") Double prezzoMin,
                                                       @Param("prezzoMax") Double prezzoMax);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "ORDER BY p.prezzo DESC")
    List<Product> findProductsWithFiltersOrderByPrezzoDesc(@Param("searchTerm") String searchTerm,
                                                           @Param("categoria") String categoria,
                                                           @Param("prezzoMin") Double prezzoMin,
                                                           @Param("prezzoMax") Double prezzoMax);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "ORDER BY p.categoria ASC, p.nome ASC")
    List<Product> findProductsWithFiltersOrderByCategoria(@Param("searchTerm") String searchTerm,
                                                          @Param("categoria") String categoria,
                                                          @Param("prezzoMin") Double prezzoMin,
                                                          @Param("prezzoMax") Double prezzoMax);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "ORDER BY p.id DESC")
    List<Product> findProductsWithFiltersOrderById(@Param("searchTerm") String searchTerm,
                                                   @Param("categoria") String categoria,
                                                   @Param("prezzoMin") Double prezzoMin,
                                                   @Param("prezzoMax") Double prezzoMax);
    
    // Query per ordinamento per rating (decrescente - più alte valutazioni prima)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "AND (:ratingMin IS NULL OR " +
           "     (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) >= :ratingMin) " +
           "ORDER BY (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) DESC, p.nome ASC")
    List<Product> findProductsWithFiltersOrderByRatingDesc(
           @Param("searchTerm") String searchTerm,
           @Param("categoria") String categoria,
           @Param("prezzoMin") Double prezzoMin,
           @Param("prezzoMax") Double prezzoMax,
           @Param("ratingMin") Integer ratingMin);
    
    // Query con filtro di rating per tutti gli altri ordinamenti
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "AND (:ratingMin IS NULL OR " +
           "     (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) >= :ratingMin) " +
           "ORDER BY p.nome ASC")
    List<Product> findProductsWithRatingFilterOrderByNome(
           @Param("searchTerm") String searchTerm,
           @Param("categoria") String categoria,
           @Param("prezzoMin") Double prezzoMin,
           @Param("prezzoMax") Double prezzoMax,
           @Param("ratingMin") Integer ratingMin);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "AND (:ratingMin IS NULL OR " +
           "     (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) >= :ratingMin) " +
           "ORDER BY p.prezzo ASC")
    List<Product> findProductsWithRatingFilterOrderByPrezzo(
           @Param("searchTerm") String searchTerm,
           @Param("categoria") String categoria,
           @Param("prezzoMin") Double prezzoMin,
           @Param("prezzoMax") Double prezzoMax,
           @Param("ratingMin") Integer ratingMin);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "AND (:ratingMin IS NULL OR " +
           "     (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) >= :ratingMin) " +
           "ORDER BY p.prezzo DESC")
    List<Product> findProductsWithRatingFilterOrderByPrezzoDesc(
           @Param("searchTerm") String searchTerm,
           @Param("categoria") String categoria,
           @Param("prezzoMin") Double prezzoMin,
           @Param("prezzoMax") Double prezzoMax,
           @Param("ratingMin") Integer ratingMin);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "AND (:ratingMin IS NULL OR " +
           "     (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) >= :ratingMin) " +
           "ORDER BY p.categoria ASC, p.nome ASC")
    List<Product> findProductsWithRatingFilterOrderByCategoria(
           @Param("searchTerm") String searchTerm,
           @Param("categoria") String categoria,
           @Param("prezzoMin") Double prezzoMin,
           @Param("prezzoMax") Double prezzoMax,
           @Param("ratingMin") Integer ratingMin);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.images " +
           "WHERE (:searchTerm = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "AND (:ratingMin IS NULL OR " +
           "     (SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.product = p) >= :ratingMin) " +
           "ORDER BY p.id DESC")
    List<Product> findProductsWithRatingFilterOrderById(
           @Param("searchTerm") String searchTerm,
           @Param("categoria") String categoria,
           @Param("prezzoMin") Double prezzoMin,
           @Param("prezzoMax") Double prezzoMax,
           @Param("ratingMin") Integer ratingMin);
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId")
    List<Rating> findRatingsForProduct(@Param("productId") Long productId);
    
    @Query("SELECT DISTINCT p FROM Product p ORDER BY p.id DESC")
    List<Product> findAllOrderByIdDesc();
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.ratings")
    List<Product> findAllWithRatings();
    
    @Query("SELECT p FROM Product p WHERE p.categoria = :categoria")
    List<Product> findByCategoria(@Param("categoria") String categoria);
}