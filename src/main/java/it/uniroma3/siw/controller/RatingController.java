package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.RatingService;
import jakarta.servlet.http.HttpSession;

@Controller
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ProductService productService;

    @PostMapping("/product/{id}/rate")
    public String rateProduct(@PathVariable Long id, @RequestParam Double value, HttpSession session, Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }

        if (product.getAutore().equals(loggedUser.getUsername())) {
            model.addAttribute("errorMessage", "Non puoi votare il tuo prodotto.");
            return "redirect:/product/" + id;
        }

        if (ratingService.hasUserRatedProduct(loggedUser, product)) {
            model.addAttribute("errorMessage", "Hai già votato questo prodotto.");
            return "redirect:/product/" + id;
        }

        ratingService.addRating(id, loggedUser, value);
        return "redirect:/product/" + id;
    }

    @PostMapping("/product/{id}/updateRating")
    public String updateRating(@PathVariable Long id, @RequestParam Double value, HttpSession session, Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }

        if (product.getAutore().equals(loggedUser.getUsername())) {
            model.addAttribute("errorMessage", "Non puoi votare il tuo prodotto.");
            return "redirect:/product/" + id;
        }

        if (!ratingService.hasUserRatedProduct(loggedUser, product)) {
            model.addAttribute("errorMessage", "Non hai ancora votato questo prodotto.");
            return "redirect:/product/" + id;
        }

        ratingService.updateRating(id, loggedUser, value);
        return "redirect:/product/" + id;
    }
}