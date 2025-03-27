package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.service.ProductService;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products/categoria")
    public String getProductsByCategoria(
            @RequestParam(required = false) String categoria,
            Model model) {
        List<Product> prodotti = productService.getProductsByCategoria(categoria);
        model.addAttribute("products", prodotti);
        model.addAttribute("categoria", categoria); // Passa anche la categoria al modello
        return "products";
    }

    @GetMapping("/products")
    public String searchProducts(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) Integer rating,
            Model model) {
        // Filtra i prodotti in base ai parametri
        List<Product> filteredProducts = productService.filterProducts(categoria, price, rating);

        // Aggiungi i prodotti filtrati al modello
        model.addAttribute("products", filteredProducts);

        // Ritorna la vista con i risultati
        return "products";
    }
}