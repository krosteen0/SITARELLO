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
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
        Rating rating = new Rating();
        rating.setProduct(product);
        rating.setUser(user);
        rating.setValue(value);

        ratingRepository.save(rating);
    }

    public boolean hasUserRatedProduct(Users user, Product product) {
        return ratingRepository.existsByUserAndProduct(user, product);
    }
}
