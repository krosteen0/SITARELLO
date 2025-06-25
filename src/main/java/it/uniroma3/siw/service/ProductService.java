package it.uniroma3.siw.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.ProductImageRepository;
import it.uniroma3.siw.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Product findById(Long id) {
        logger.debug("Ricerca prodotto con ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    // Forza il caricamento delle liste ratings e images
                    if (product.getRatings() != null) {
                        product.getRatings().size();
                    }
                    if (product.getImages() != null) {
                        product.getImages().size();
                    }
                    logger.debug("Prodotto trovato con ID: {}", id);
                    return product;
                })
                .orElse(null);
    }

    public void deleteProduct(Long id) {
        logger.info("Eliminazione prodotto con ID: {}", id);
        productRepository.deleteById(id);
        logger.debug("Prodotto con ID {} eliminato con successo", id);
    }

    public Iterable<Product> getAllProducts() {
        logger.debug("Recupero di tutti i prodotti");
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
        logger.info("Salvataggio prodotto: {}", product.getNome());
        productRepository.save(product);
        logger.debug("Prodotto {} salvato con successo", product.getNome());
    }

    public List<Product> getProductsByCategoria(String categoria) {
        logger.debug("Recupero prodotti per categoria: {}", categoria);
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
        logger.debug("Trovati {} prodotti per categoria: {}", products.size(), categoria);
        return products;
    }

    public List<Product> filterProducts(String categoria, Integer price, Integer rating) {
        logger.debug("Filtraggio prodotti con categoria: {}, prezzo: {}, rating: {}", categoria, price, rating);
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

    public List<Product> findProductsByAutore(Users autore) {
        logger.debug("Recupero prodotti per autore: {}", autore);
        List<Product> products = productRepository.findByAutore(autore);
        products.forEach(product -> {
            if (product.getRatings() != null) {
                product.getRatings().size();
            }
            if (product.getImages() != null) {
                product.getImages().size();
            }
        });
        logger.debug("Trovati {} prodotti per autore: {}", products.size(), autore);
        return products;
    }

    @Transactional
    public void removeImageFromProduct(Product product, Long imageId) {
        if (product == null || product.getImages() == null) {
            logger.error("Prodotto o lista immagini nulli per productId: {}", product != null ? product.getId() : null);
            throw new IllegalArgumentException("Prodotto o immagini non validi");
        }

        ProductImage imageToRemove = product.getImages().stream()
                .filter(img -> img != null && img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Immagine con ID {} non trovata per il prodotto {}", imageId, product.getId());
                    return new IllegalArgumentException("Immagine non trovata");
                });

        product.getImages().remove(imageToRemove);

        // Elimina eventuali null residui dalla lista
        product.getImages().removeIf(img -> img == null);

        try {
            fileStorageService.deleteFile(imageToRemove.getFilePath());
            logger.info("File fisico {} eliminato con successo", imageToRemove.getFilePath());
        } catch (Exception e) {
            logger.warn("Errore durante l'eliminazione del file fisico {}: {}", imageToRemove.getFilePath(), e.getMessage());
        }

        productImageRepository.delete(imageToRemove);
        logger.info("Immagine con ID {} eliminata dal database", imageId);

        productRepository.save(product);
        logger.info("Prodotto con ID {} aggiornato dopo la rimozione dell'immagine", product.getId());
    }
}