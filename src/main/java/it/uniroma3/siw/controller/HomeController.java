package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.ProductImageRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.UsersRepository;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }
        try {
            // 1. Recupera i prodotti più recenti (max 10)
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
            // 3. Raggruppa prodotti per categoria (top categorie)
            List<String> allCategories = productRepository.findDistinctCategories();
            if (allCategories == null) allCategories = new ArrayList<>();
            Map<String, List<Product>> productsByCategory = new HashMap<>();
            int categoryLimit = Math.min(allCategories.size(), 6);
            List<String> topCategories = allCategories.isEmpty() ? new ArrayList<>() : allCategories.subList(0, categoryLimit);
            for (String category : topCategories) {
                List<Product> categoryProducts = productRepository.findByCategoria(category);
                if (categoryProducts == null) categoryProducts = new ArrayList<>();
                if (!categoryProducts.isEmpty()) {
                    if (categoryProducts.size() > 4) {
                        categoryProducts = categoryProducts.subList(0, 4);
                    }
                    loadImagesForProducts(categoryProducts);
                    productsByCategory.put(category, categoryProducts);
                }
            }
            // 4. Aggiungi prodotti consigliati per utente autenticato
            if (isAuthenticated && auth != null) {
                Users currentUser = usersRepository.findByUsername(auth.getName()).orElse(null);
                if (currentUser != null) {
                    List<Product> tempRecommendedProducts = new ArrayList<>();
                    List<String> remainingCategories = new ArrayList<>(allCategories);
                    remainingCategories.removeAll(topCategories.subList(0, Math.min(2, topCategories.size())));
                    if (!remainingCategories.isEmpty()) {
                        Collections.shuffle(remainingCategories);
                        int categoriesToShow = Math.min(2, remainingCategories.size());
                        for (int i = 0; i < categoriesToShow; i++) {
                            String category = remainingCategories.get(i);
                            List<Product> products = productRepository.findByCategoria(category);
                            if (products == null) products = new ArrayList<>();
                            if (!products.isEmpty()) {
                                Collections.shuffle(products);
                                int productsToAdd = Math.min(3, products.size());
                                tempRecommendedProducts.addAll(products.subList(0, productsToAdd));
                            }
                        }
                    }
                    if (tempRecommendedProducts.size() < 4) {
                        final List<Product> finalTempRecommendedProducts = tempRecommendedProducts;
                        List<Product> additionalProducts = featuredProducts.stream()
                                .filter(p -> !finalTempRecommendedProducts.contains(p))
                                .collect(Collectors.toList());
                        int neededProducts = 4 - tempRecommendedProducts.size();
                        if (additionalProducts.size() > neededProducts) {
                            additionalProducts = additionalProducts.subList(0, neededProducts);
                        }
                        tempRecommendedProducts.addAll(additionalProducts);
                    }
                    List<Product> finalRecommendations = tempRecommendedProducts;
                    if (tempRecommendedProducts.size() > 6) {
                        finalRecommendations = new ArrayList<>(tempRecommendedProducts.subList(0, 6));
                    }
                    loadImagesForProducts(finalRecommendations);
                    model.addAttribute("recommendedProducts", finalRecommendations);
                }
            }
            // Assicurati che tutte le variabili siano sempre presenti nel model
            model.addAttribute("recentProducts", recentProducts != null ? recentProducts : new ArrayList<>());
            model.addAttribute("featuredProducts", featuredProducts != null ? featuredProducts : new ArrayList<>());
            model.addAttribute("productsByCategory", productsByCategory != null ? productsByCategory : new HashMap<>());
        } catch (Exception e) {
            model.addAttribute("errorLoadingData", true);
            model.addAttribute("recentProducts", new ArrayList<>());
            model.addAttribute("featuredProducts", new ArrayList<>());
            model.addAttribute("productsByCategory", new HashMap<>());
            e.printStackTrace();
        }
        return "index";
    }
    
    /**
     * Carica manualmente le immagini per ogni prodotto
     */
    private void loadImagesForProducts(List<Product> products) {
        for (Product product : products) {
            List<ProductImage> images = productImageRepository.findByProductId(product.getId());
            product.setImages(images);
        }
    }
}
