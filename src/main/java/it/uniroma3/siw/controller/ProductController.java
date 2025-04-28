package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
            HttpSession session) {
        // Recupera l'utente loggato dalla sessione
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Reindirizza alla pagina di login se l'utente non è loggato
        }

        // Imposta l'autore del prodotto
        product.setAutore(loggedUser.getUsername());

        // Salva il prodotto
        productService.saveProduct(product);

        return "redirect:/my-products"; // Reindirizza alla pagina "I Miei Prodotti"
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, Model model) {
        // Recupera l'utente loggato dalla sessione
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Reindirizza al login se l'utente non è loggato
        }

        // Verifica che il prodotto appartenga all'utente loggato
        Product product = productService.findById(id);
        if (product != null && product.getAutore().equals(loggedUser.getUsername())) {
            productService.deleteProduct(id); // Elimina il prodotto
        } else {
            model.addAttribute("errorMessage", "Non puoi rimuovere questo prodotto.");
        }

        return "redirect:/my-products"; // Reindirizza alla pagina "I Miei Prodotti"
    }

    @GetMapping("/formNewProduct")
    public String showFormNewProduct(Model model) {
        model.addAttribute("product", new Product()); // Aggiunge un oggetto vuoto al modello
        return "formNewProduct"; // Nome del template HTML
    }

    @GetMapping("/my-products")
public String getMyProducts(HttpSession session, Model model, HttpServletRequest request) {
    // Recupera l'utente loggato dalla sessione
    Users loggedUser = (Users) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        return "redirect:/login"; // Reindirizza al login se l'utente non è loggato
    }

    // Recupera i prodotti creati dall'utente loggato
    List<Product> myProducts = productService.findProductsByAutore(loggedUser.getUsername());
    model.addAttribute("products", myProducts);
    model.addAttribute("redirectUrl", request.getRequestURI()); // Passa redirectUrl al modello

    return "myProducts"; // Ritorna il template myProducts.html
}

    // Mostra la pagina di modifica del prodotto
    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model, @RequestParam(required = false) String redirectUrl) {
        // Recupera l'utente loggato dalla sessione
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Reindirizza al login se l'utente non è loggato
        }

        // Recupera il prodotto da modificare
        Product product = productService.findById(id);
        if (product == null || !product.getAutore().equals(loggedUser.getUsername())) {
            return "redirect:/my-products"; // Reindirizza se il prodotto non appartiene all'utente
        }

        model.addAttribute("product", product);
        model.addAttribute("redirectUrl", redirectUrl); // Aggiungi il redirectUrl al modello
        return "editProduct"; // Ritorna il template editProduct.html
    }

    // Salva le modifiche al prodotto
    @PostMapping("/product/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(required = false) String redirectUrl,
            HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Reindirizza al login se l'utente non è loggato
        }

        Product existingProduct = productService.findById(id);
        if (existingProduct == null || !existingProduct.getAutore().equals(loggedUser.getUsername())) {
            return "redirect:/my-products"; // Reindirizza se il prodotto non appartiene all'utente
        }

        // Aggiorna i campi modificabili
        existingProduct.setNome(product.getNome());
        existingProduct.setCategoria(product.getCategoria());
        existingProduct.setPrezzo(product.getPrezzo());
        existingProduct.setDescrizione(product.getDescrizione());
        existingProduct.setFoto(product.getFoto());

        productService.saveProduct(existingProduct); // Salva le modifiche

        // Reindirizza alla pagina precedente o a "my-products" se il redirectUrl è nullo
        return (redirectUrl != null && !redirectUrl.isEmpty()) ? "redirect:" + redirectUrl : "redirect:/my-products";
    }

    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable Long id, HttpServletRequest request, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products"; // Reindirizza alla lista dei prodotti se il prodotto non esiste
        }
        model.addAttribute("product", product);
        model.addAttribute("redirectUrl", request.getHeader("Referer")); // Usa l'header Referer per tornare alla pagina precedente
        return "product"; // Ritorna il template product.html
    }
}