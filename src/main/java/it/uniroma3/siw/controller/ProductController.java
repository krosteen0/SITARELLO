package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/product")
    public String addProduct(
            @ModelAttribute Product product,
            Model model) {
        try {
            // Salva il prodotto
            productService.saveProduct(product);
            // Aggiungi un messaggio di conferma al modello
            model.addAttribute("successMessage", "Prodotto aggiunto con successo!");
        } catch (Exception e) {
            // Aggiungi un messaggio di errore al modello
            model.addAttribute("errorMessage", "Errore durante l'aggiunta del prodotto.");
            e.printStackTrace();
        }
        return "formNewProduct"; // Ritorna alla pagina del modulo
    }

    @GetMapping("/formNewProduct")
    public String showFormNewProduct(Model model) {
        model.addAttribute("product", new Product()); // Aggiunge un oggetto vuoto al modello
        return "formNewProduct"; // Nome del template HTML
    }
}