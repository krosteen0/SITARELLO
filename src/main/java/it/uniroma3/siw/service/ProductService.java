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
                    // Forza il caricamento della lista ratings
                    if (product.getRatings() != null) {
                        product.getRatings().size(); // Inizializza la lista
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
                    // Forza il caricamento della lista ratings
                    if (product.getRatings() != null) {
                        product.getRatings().size();
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
        // Forza il caricamento della lista ratings per ogni prodotto
        products.forEach(product -> {
            if (product.getRatings() != null) {
                product.getRatings().size();
            }
        });
        return products;
    }

    public List<Product> filterProducts(String categoria, Integer price, Integer rating) {
        // Recupera tutti i prodotti
        List<Product> allProducts = productRepository.findAll();

        // Filtra i prodotti in base ai parametri
        return allProducts.stream()
                .filter(product -> categoria == null || product.getCategoria().equalsIgnoreCase(categoria))
                .filter(product -> price == null || product.getPrezzo() <= price)
                .filter(product -> rating == null || product.getAverageRating() >= rating) // Modificato da getRating a getAverageRating
                .peek(product -> {
                    // Forza il caricamento della lista ratings
                    if (product.getRatings() != null) {
                        product.getRatings().size();
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Product> findProductsByAutore(String autore) {
        List<Product> products = productRepository.findByAutore(autore);
        // Forza il caricamento della lista ratings per ogni prodotto
        products.forEach(product -> {
            if (product.getRatings() != null) {
                product.getRatings().size();
            }
        });
        return products;
    }
}