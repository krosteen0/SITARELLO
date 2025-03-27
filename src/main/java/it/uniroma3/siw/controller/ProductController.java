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
}