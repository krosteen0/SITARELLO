package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.model.Category;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.repository.CategoryRepository;
import it.uniroma3.siw.repository.ProductImageRepository;
import it.uniroma3.siw.repository.ProductRepository;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }
        
        try {
            // 1. Recupera i prodotti più recenti (max 8)
            List<Product> recentProducts = productRepository.findAllOrderByIdDesc();
            if (recentProducts == null) recentProducts = new ArrayList<>();
            if (recentProducts.size() > 8) {
                recentProducts = recentProducts.subList(0, 8);
            }
            loadImagesForProducts(recentProducts);
            
            // 2. Recupera i prodotti in evidenza (con rating più alto, max 4)
            List<Product> featuredProducts = productRepository.findAllWithRatings();
            if (featuredProducts == null) featuredProducts = new ArrayList<>();
            for (Product product : featuredProducts) {
                product.setRatings(productRepository.findRatingsForProduct(product.getId()));
            }
            featuredProducts.sort((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()));
            if (featuredProducts.size() > 4) {
                featuredProducts = featuredProducts.subList(0, 4);
            }
            loadImagesForProducts(featuredProducts);
            
            // 3. Recupera le categorie con il conteggio dei prodotti
            List<Category> categories = (List<Category>) categoryRepository.findAll();
            for (Category category : categories) {
                long productCount = productRepository.findByCategory(category).size();
                category.setProductCount(productCount);
            }
            
            // Assicurati che tutte le variabili siano sempre presenti nel model
            model.addAttribute("recentProducts", recentProducts);
            model.addAttribute("featuredProducts", featuredProducts);
            model.addAttribute("categories", categories);
            
        } catch (Exception e) {
            model.addAttribute("errorLoadingData", true);
            model.addAttribute("recentProducts", new ArrayList<>());
            model.addAttribute("featuredProducts", new ArrayList<>());
            model.addAttribute("categories", new ArrayList<>());
            System.err.println("Error loading homepage data: " + e.getMessage());
        }
        
        return "index";
    }

    private void loadImagesForProducts(List<Product> products) {
        for (Product product : products) {
            try {
                List<ProductImage> images = productImageRepository.findByProduct(product);
                if (images != null && !images.isEmpty()) {
                    ProductImage firstImage = images.get(0);
                    if (firstImage.getImageData() != null) {
                        product.setBase64Image(java.util.Base64.getEncoder().encodeToString(firstImage.getImageData()));
                    }
                }
            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error loading image for product " + product.getId() + ": " + e.getMessage());
            }
        }
    }
}
