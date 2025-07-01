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
import it.uniroma3.siw.repository.ProductRepository;

@Controller
@RequestMapping("/products")
public class ProductSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductSearchController.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    private void addAuthenticationAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
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
            
            // Esegue la ricerca con i filtri
            List<Product> products = productRepository.findProductsWithFiltersAndSort(
                searchDTO.hasSearchTerm() ? searchDTO.getSearchTerm() : null,
                searchDTO.hasCategoria() ? searchDTO.getCategoria() : null,
                searchDTO.hasPrezzoMin() ? searchDTO.getPrezzoMin() : null,
                searchDTO.hasPrezzoMax() ? searchDTO.getPrezzoMax() : null,
                searchDTO.getSortBy()
            );
            
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
            model.addAttribute("errorMessage", "Errore nella ricerca dei prodotti.");
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
                products = productRepository.findProductsWithFiltersAndSort(q, null, null, null, "nome");
            } else {
                products = productRepository.findAllWithImages();
            }
            
            List<String> categories = productRepository.findDistinctCategories();
            ProductSearchDTO searchDTO = new ProductSearchDTO();
            searchDTO.setSearchTerm(q);
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("searchDTO", searchDTO);
            model.addAttribute("totalProducts", products.size());
            
            return "products-search";
            
        } catch (Exception e) {
            logger.error("Error in quick search: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Errore nella ricerca rapida.");
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
