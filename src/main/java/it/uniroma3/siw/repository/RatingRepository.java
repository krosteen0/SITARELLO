package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.model.Users;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProduct(Product product);
    boolean existsByUserAndProduct(Users user, Product product);
    Rating findByUserAndProduct(Users user, Product product);
}