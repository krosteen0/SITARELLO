package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Users;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.autore = :autore")
    List<Product> findByAutoreWithImages(@Param("autore") Users autore);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images")
    List<Product> findAllWithImages();
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.autore WHERE p.id = :id")
    Optional<Product> findByIdWithAutore(@Param("id") Long id);
    
    List<Product> findByAutore(Users autore);
    
    // Query per ricerca e filtri
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE (:searchTerm IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
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
           "WHERE (:searchTerm IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
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
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE (:searchTerm IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
           "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'nome' THEN p.nome END ASC, " +
           "CASE WHEN :sortBy = 'prezzo' THEN p.prezzo END ASC, " +
           "CASE WHEN :sortBy = 'prezzo_desc' THEN p.prezzo END DESC, " +
           "CASE WHEN :sortBy = 'categoria' THEN p.categoria END ASC, " +
           "p.id DESC")
    List<Product> findProductsWithFiltersAndSort(@Param("searchTerm") String searchTerm,
                                                 @Param("categoria") String categoria,
                                                 @Param("prezzoMin") Double prezzoMin,
                                                 @Param("prezzoMax") Double prezzoMax,
                                                 @Param("sortBy") String sortBy);
}