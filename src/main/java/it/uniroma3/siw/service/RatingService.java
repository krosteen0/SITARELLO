package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.RatingRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    public void addRating(Long productId, Users user, Double value) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
        Rating rating = new Rating();
        rating.setProduct(product);
        rating.setUser(user);
        rating.setValue(value);

        // Aggiungi il rating alla lista del prodotto (per mantenere la coerenza)
        if (product.getRatings() == null) {
            product.setRatings(new java.util.ArrayList<>());
        }
        product.getRatings().add(rating);

        ratingRepository.save(rating);
        // Non c'è bisogno di aggiornare averageRating, poiché è calcolato dinamicamente
    }

    public boolean hasUserRatedProduct(Users user, Product product) {
        return ratingRepository.existsByUserAndProduct(user, product);
    }

    public Double getUserRating(Users user, Product product) {
        Rating rating = ratingRepository.findByUserAndProduct(user, product);
        return rating != null ? rating.getValue() : null;
    }

    public void updateRating(Long productId, Users user, Double value) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
        Rating rating = ratingRepository.findByUserAndProduct(user, product);
        if (rating != null) {
            rating.setValue(value);
            ratingRepository.save(rating);
            // Non c'è bisogno di aggiornare averageRating, poiché è calcolato dinamicamente
        }
    }
}