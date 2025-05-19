package it.uniroma3.siw.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product findById(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    // Forza il caricamento delle liste ratings e images
                    if (product.getRatings() != null) {
                        product.getRatings().size();
                    }
                    if (product.getImages() != null) {
                        product.getImages().size();
                    }
                    return product;
                })
                .orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .peek(product -> {
                    if (product.getRatings() != null) {
                        product.getRatings().size();
                    }
                    if (product.getImages() != null) {
                        product.getImages().size();
                    }
                })
                .collect(Collectors.toList());
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
    
    public List<Product> getProductsByCategoria(String categoria) {
        List<Product> products;
        if (categoria != null && !categoria.isEmpty()) {
            products = productRepository.findByCategoria(categoria);
        } else {
            products = productRepository.findAll();
        }
        products.forEach(product -> {
            if (product.getRatings() != null) {
                product.getRatings().size();
            }
            if (product.getImages() != null) {
                product.getImages().size();
            }
        });
        return products;
    }

    public List<Product> filterProducts(String categoria, Integer price, Integer rating) {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(product -> categoria == null || product.getCategoria().equalsIgnoreCase(categoria))
                .filter(product -> price == null || product.getPrezzo() <= price)
                .filter(product -> rating == null || product.getAverageRating() >= rating)
                .peek(product -> {
                    if (product.getRatings() != null) {
                        product.getRatings().size();
                    }
                    if (product.getImages() != null) {
                        product.getImages().size();
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Product> findProductsByAutore(String autore) {
        List<Product> products = productRepository.findByAutore(autore);
        products.forEach(product -> {
            if (product.getRatings() != null) {
                product.getRatings().size();
            }
            if (product.getImages() != null) {
                product.getImages().size();
            }
        });
        return products;
    }
}