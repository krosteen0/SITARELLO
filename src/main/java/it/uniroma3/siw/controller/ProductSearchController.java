package it.uniroma3.siw.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.dto.ProductSearchDTO;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.repository.ProductImageRepository;
import it.uniroma3.siw.repository.ProductRepository;

@Controller
@RequestMapping("/products")
public class ProductSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductSearchController.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    private void addAuthenticationAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }
    }
    
    /**
     * Carica manualmente le immagini per ogni prodotto per evitare problemi con FETCH JOIN
     */
    private void loadImagesForProducts(List<Product> products) {
        for (Product product : products) {
            List<ProductImage> images = productImageRepository.findByProductId(product.getId());
            product.setImages(images);
        }
    }
    
    /**
     * Visualizza tutti i prodotti con possibilità di ricerca e filtri
     */
    @GetMapping
    public String showAllProducts(Model model) {
        try {
            addAuthenticationAttributes(model);
            
            // Carica tutti i prodotti senza filtri
            List<Product> products = productRepository.findAllWithImages();
            // Carica manualmente le immagini per tutti i prodotti
            loadImagesForProducts(products);
            
            List<String> categories = productRepository.findDistinctCategories();
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("searchDTO", new ProductSearchDTO());
            model.addAttribute("totalProducts", products.size());
            
            logger.info("Loaded {} products for public view", products.size());
            return "products-search";
            
        } catch (Exception e) {
            logger.error("Error loading products: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Errore nel caricamento dei prodotti.");
            return "error";
        }
    }
    
    /**
     * Gestisce la ricerca e i filtri sui prodotti
     */
    @GetMapping("/search")
    public String searchProducts(@ModelAttribute ProductSearchDTO searchDTO, Model model) {
        try {
            addAuthenticationAttributes(model);
            
            logger.info("Searching products with filters: {}", searchDTO);
            
            // Parametri per la ricerca - gestisce gli elementi vuoti
            String searchTerm = searchDTO.hasSearchTerm() ? searchDTO.getSearchTerm() : "";
            String categoria = searchDTO.hasCategoria() ? searchDTO.getCategoria() : null;
            Double prezzoMin = searchDTO.hasPrezzoMin() ? searchDTO.getPrezzoMin() : null;
            Double prezzoMax = searchDTO.hasPrezzoMax() ? searchDTO.getPrezzoMax() : null;
            Integer ratingMin = searchDTO.hasRatingMin() ? searchDTO.getRatingMin() : null;
            String sortBy = searchDTO.getSortBy() != null ? searchDTO.getSortBy() : "id";
            
            // Esegue la ricerca con i filtri usando il metodo appropriato
            List<Product> products;
            
            try {
                // Per l'ordinamento per rating, non usiamo la query diretta perché è problematica
                // Invece recuperiamo i prodotti con filtri e poi li ordiniamo manualmente
                boolean sortByRating = "rating".equals(sortBy);
                String effectiveSortBy = sortByRating ? "id" : sortBy; // Se l'ordinamento è per rating, usiamo id come default
                
                if (ratingMin != null && ratingMin > 0) {
                    // Se c'è un filtro di rating, usa la query con filtro rating
                    products = switch (effectiveSortBy) {
                        case "nome" -> productRepository.findProductsWithRatingFilterOrderByNome(
                                searchTerm, categoria, prezzoMin, prezzoMax, ratingMin);
                        case "prezzo" -> productRepository.findProductsWithRatingFilterOrderByPrezzo(
                                searchTerm, categoria, prezzoMin, prezzoMax, ratingMin);
                        case "prezzo_desc" -> productRepository.findProductsWithRatingFilterOrderByPrezzoDesc(
                                searchTerm, categoria, prezzoMin, prezzoMax, ratingMin);
                        case "categoria" -> productRepository.findProductsWithRatingFilterOrderByCategoria(
                                searchTerm, categoria, prezzoMin, prezzoMax, ratingMin);
                        default -> productRepository.findProductsWithRatingFilterOrderById(
                                searchTerm, categoria, prezzoMin, prezzoMax, ratingMin);
                    };
                } else {
                    // Altrimenti usa le query normali
                    products = switch (effectiveSortBy) {
                        case "nome" -> productRepository.findProductsWithFiltersOrderByNome(
                                searchTerm, categoria, prezzoMin, prezzoMax);
                        case "prezzo" -> productRepository.findProductsWithFiltersOrderByPrezzo(
                                searchTerm, categoria, prezzoMin, prezzoMax);
                        case "prezzo_desc" -> productRepository.findProductsWithFiltersOrderByPrezzoDesc(
                                searchTerm, categoria, prezzoMin, prezzoMax);
                        case "categoria" -> productRepository.findProductsWithFiltersOrderByCategoria(
                                searchTerm, categoria, prezzoMin, prezzoMax);
                        default -> productRepository.findProductsWithFiltersOrderById(
                                searchTerm, categoria, prezzoMin, prezzoMax);
                    };
                }
                
                // Carica manualmente le immagini per tutti i prodotti
                loadImagesForProducts(products);
                
                // Carica manualmente i ratings per ogni prodotto
                for (Product product : products) {
                    product.setRatings(productRepository.findRatingsForProduct(product.getId()));
                }
                
                // Se l'ordinamento è per rating, ordina i prodotti a livello di applicazione
                if (sortByRating) {
                    products.sort((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()));
                }
                
            } catch (Exception e) {
                logger.error("Error in product search: {}", e.getMessage(), e);
                throw e; // Ripropaga l'eccezione per il gestione dell'errore generale
            }
            
            // Carica le categorie per il dropdown
            List<String> categories = productRepository.findDistinctCategories();
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("searchDTO", searchDTO);
            model.addAttribute("totalProducts", products.size());
            model.addAttribute("hasFilters", searchDTO.hasFilters());
            
            logger.info("Found {} products matching search criteria", products.size());
            return "products-search";
            
        } catch (Exception e) {
            logger.error("Error searching products: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Errore nella ricerca dei prodotti: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * API endpoint per ricerca rapida (per auto-completamento o AJAX)
     */
    @GetMapping("/quick-search")
    public String quickSearch(@RequestParam(required = false) String q, Model model) {
        try {
            addAuthenticationAttributes(model);
            
            List<Product> products;
            if (q != null && !q.trim().isEmpty()) {
                products = productRepository.findProductsWithFiltersOrderByNome(q, null, null, null);
            } else {
                products = productRepository.findAllWithImages();
            }
            
            // Carica manualmente le immagini per tutti i prodotti
            loadImagesForProducts(products);
            
            List<String> categories = productRepository.findDistinctCategories();
            ProductSearchDTO searchDTO = new ProductSearchDTO();
            searchDTO.setSearchTerm(q);
            searchDTO.setSortBy("nome");
            searchDTO.setRatingMin(null);
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("searchDTO", searchDTO);
            model.addAttribute("totalProducts", products.size());
            
            return "products-search";
            
        } catch (Exception e) {
            logger.error("Error in quick search: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Errore nella ricerca rapida: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Reset dei filtri - mostra tutti i prodotti
     */
    @GetMapping("/reset")
    public String resetFilters(Model model) {
        return "redirect:/products";
    }
}
