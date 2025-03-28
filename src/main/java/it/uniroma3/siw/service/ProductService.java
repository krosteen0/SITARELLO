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

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void saveProduct(Product product) {
        productRepository.save(product); // Salva il prodotto nel database
    }
    
    public List<Product> getProductsByCategoria(String categoria) {
        if (categoria != null && !categoria.isEmpty()) {
            return productRepository.findByCategoria(categoria);
        } else {
            return productRepository.findAll(); // Restituisce tutti i prodotti se la categoria non è specificata
        }
    }

    public List<Product> filterProducts(String categoria, Integer price, Integer rating) {
        // Recupera tutti i prodotti
        List<Product> allProducts = productRepository.findAll();

        // Filtra i prodotti in base ai parametri
        return allProducts.stream()
                .filter(product -> categoria == null || product.getCategoria().equalsIgnoreCase(categoria))
                .filter(product -> price == null || product.getPrezzo() <= price)
                .filter(product -> rating == null || product.getRating() >= rating)
                .collect(Collectors.toList());
    }
}