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
import it.uniroma3.siw.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingService ratingService; // Aggiunto per gestire i voti

    @GetMapping("/products/categoria")
    public String getProductsByCategoria(
            @RequestParam(required = false) String categoria,
            Model model) {
        List<Product> prodotti = productService.getProductsByCategoria(categoria);
        model.addAttribute("products", prodotti);
        model.addAttribute("categoria", categoria);
        model.addAttribute("redirectUrl", categoria != null ? "/products/categoria?categoria=" + categoria : "/products");
        return "products";
    }

    @GetMapping("/products")
    public String searchProducts(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) Integer rating,
            Model model) {
        List<Product> filteredProducts = productService.filterProducts(categoria, price, rating);
        model.addAttribute("products", filteredProducts);
        StringBuilder redirectUrl = new StringBuilder("/products");
        boolean hasParams = false;
        if (categoria != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("categoria=").append(categoria);
            hasParams = true;
        }
        if (price != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("price=").append(price);
            hasParams = true;
        }
        if (rating != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("rating=").append(rating);
        }
        model.addAttribute("redirectUrl", redirectUrl.toString());
        return "products";
    }

    @PostMapping("/product")
    public String addProduct(
            @ModelAttribute Product product,
            HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        product.setAutore(loggedUser.getUsername());
        productService.saveProduct(product);
        return "redirect:/my-products";
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product != null && product.getAutore().equals(loggedUser.getUsername())) {
            productService.deleteProduct(id);
        } else {
            model.addAttribute("errorMessage", "Non puoi rimuovere questo prodotto.");
        }
        return "redirect:/my-products";
    }

    @GetMapping("/formNewProduct")
    public String showFormNewProduct(Model model) {
        model.addAttribute("product", new Product());
        return "formNewProduct";
    }

    @GetMapping("/my-products")
    public String getMyProducts(HttpSession session, Model model, HttpServletRequest request) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        List<Product> myProducts = productService.findProductsByAutore(loggedUser.getUsername());
        model.addAttribute("products", myProducts);
        model.addAttribute("redirectUrl", request.getRequestURI());
        return "myProducts";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model, @RequestParam(required = false) String redirectUrl) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product == null || !product.getAutore().equals(loggedUser.getUsername())) {
            return "redirect:/my-products";
        }
        model.addAttribute("product", product);
        model.addAttribute("redirectUrl", redirectUrl);
        return "editProduct";
    }

    @PostMapping("/product/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(required = false) String redirectUrl,
            HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product existingProduct = productService.findById(id);
        if (existingProduct == null || !existingProduct.getAutore().equals(loggedUser.getUsername())) {
            return "redirect:/my-products";
        }
        existingProduct.setNome(product.getNome());
        existingProduct.setCategoria(product.getCategoria());
        existingProduct.setPrezzo(product.getPrezzo());
        existingProduct.setDescrizione(product.getDescrizione());
        existingProduct.setFoto(product.getFoto());
        productService.saveProduct(existingProduct);
        return (redirectUrl != null && !redirectUrl.isEmpty()) ? "redirect:" + redirectUrl : "redirect:/my-products";
    }

    @GetMapping("/product/{id}")
    public String getProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String redirectUrl,
            HttpSession session,
            HttpServletRequest request,
            Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }

        Users loggedUser = (Users) session.getAttribute("loggedUser");
        boolean hasRated = loggedUser != null && ratingService.hasUserRatedProduct(loggedUser, product);
        Double userRating = null;
        if (hasRated) {
            userRating = ratingService.getUserRating(loggedUser, product);
        }

        String finalRedirectUrl = redirectUrl != null && !redirectUrl.isEmpty() ? redirectUrl : request.getHeader("Referer");
        if (finalRedirectUrl == null || finalRedirectUrl.contains("/product/" + id)) {
            finalRedirectUrl = "/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("hasRated", hasRated);
        model.addAttribute("userRating", userRating);
        model.addAttribute("redirectUrl", finalRedirectUrl);

        return "product";
    }
}