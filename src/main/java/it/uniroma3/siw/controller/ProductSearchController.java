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
import it.uniroma3.siw.model.Category;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.repository.CategoryRepository;
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
    
    @Autowired
    private CategoryRepository categoryRepository;
    
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
            List<Product> products = productRepository.findAll();
            // Carica manualmente le immagini per tutti i prodotti
            loadImagesForProducts(products);
            
            List<Category> categories = (List<Category>) categoryRepository.findAll();
            
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
            Category category = null;
            if (searchDTO.hasCategoria()) {
                // Convert string categoria to Category entity
                List<Category> allCategories = (List<Category>) categoryRepository.findAll();
                category = allCategories.stream()
                    .filter(c -> c.getName().equals(searchDTO.getCategoria()))
                    .findFirst()
                    .orElse(null);
            }
            Double priceMin = searchDTO.hasPrezzoMin() ? searchDTO.getPrezzoMin() : null;
            Double priceMax = searchDTO.hasPrezzoMax() ? searchDTO.getPrezzoMax() : null;
            
            // Esegue la ricerca con i filtri
            List<Product> products = productRepository.findProductsWithFilters(
                searchTerm, category, priceMin, priceMax);
            
            // Carica manualmente le immagini per tutti i prodotti
            loadImagesForProducts(products);
            
            // Carica manualmente i ratings per ogni prodotto
            for (Product product : products) {
                product.setRatings(productRepository.findRatingsForProduct(product.getId()));
            }
            
            // Ordinamento manuale
            String sortBy = searchDTO.getSortBy() != null ? searchDTO.getSortBy() : "id";
            switch (sortBy) {
                case "name" -> products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                case "price" -> products.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                case "price_desc" -> products.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                case "rating" -> products.sort((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()));
                default -> products.sort((p1, p2) -> Long.compare(p2.getId(), p1.getId()));
            }
            
            // Carica le categorie per il dropdown
            List<Category> categories = (List<Category>) categoryRepository.findAll();
            List<String> categoryNames = categories.stream().map(Category::getName).toList();
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categoryNames);
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
                products = productRepository.findProductsWithFilters(q, null, null, null);
            } else {
                products = productRepository.findAll();
            }
            
            // Carica manualmente le immagini per tutti i prodotti
            loadImagesForProducts(products);
            
            List<Category> categories = (List<Category>) categoryRepository.findAll();
            ProductSearchDTO searchDTO = new ProductSearchDTO();
            searchDTO.setSearchTerm(q);
            searchDTO.setSortBy("name");
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
