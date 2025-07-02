package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query("SELECT c FROM Category c JOIN c.products p GROUP BY c ORDER BY COUNT(p) DESC")
    List<Category> findTopCategories();
    
    Optional<Category> findByName(String name);

}
