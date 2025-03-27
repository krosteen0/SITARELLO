package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getProducts(String categoria, Integer price, Integer rating) {
        if (categoria != null && price != null && rating != null) {
            return productRepository.findByCategoriaAndPriceLessThanEqualAndRatingGreaterThanEqual(categoria, price, rating);
        } else if (categoria != null && price != null) {
            return productRepository.findByCategoriaAndPriceLessThanEqual(categoria, price);
        } else if (categoria != null && rating != null) {
            return productRepository.findByCategoriaAndRatingGreaterThanEqual(categoria, rating);
        } else if (categoria != null) {
            return productRepository.findByCategoria(categoria);
        } else {
            return productRepository.findAll();
        }
    }
}