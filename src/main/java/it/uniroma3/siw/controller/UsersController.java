package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.dto.UsersDTO;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.service.UsersService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;
    
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

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        addAuthenticationAttributes(model);
        model.addAttribute("usersDTO", new UsersDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UsersDTO usersDTO,
                              BindingResult bindingResult, Model model) {
        addAuthenticationAttributes(model);
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            usersService.saveUser(usersDTO);
            return "redirect:/users/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Errore durante la registrazione. Riprova.");
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        addAuthenticationAttributes(model);
        return "login";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        try {
            addAuthenticationAttributes(model);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                
                // Get the username
                String username = authentication.getName();
                
                // Retrieve the user from database directly instead of using principal
                Users user = usersService.findByUsername(username);
                
                if (user != null) {
                    model.addAttribute("user", user);
                    return "profile";
                }
            }
            
            // Se l'utente non è autenticato correttamente, reindirizza al login
            return "redirect:/users/login";
            
        } catch (Exception e) {
            // Log the error in a safe way
            System.err.println("Errore nel caricamento del profilo: " + e.getMessage());
            model.addAttribute("errorMessage", "Errore nel caricamento del profilo: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/products")
    @Transactional(readOnly = true)
    public String showUserProducts(Model model) {
        try {
            addAuthenticationAttributes(model);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                
                // Get the username
                String username = authentication.getName();
                
                // Retrieve the user from database directly
                Users user = usersService.findByUsername(username);
                
                if (user != null) {
                    model.addAttribute("products", productRepository.findByAutoreWithImages(user));
                    model.addAttribute("user", user);
                    return "products";
                }
            }
            
            return "redirect:/users/login";
            
        } catch (Exception e) {
            // Log the error in a safe way
            System.err.println("Errore nel caricamento dei prodotti: " + e.getMessage());
            model.addAttribute("errorMessage", "Impossibile caricare i tuoi prodotti. Riprova più tardi.");
            return "error";
        }
    }

    @GetMapping("/seller/{username}")
    @Transactional(readOnly = true)
    public String showSellerProfile(@PathVariable String username, Model model) {
        try {
            addAuthenticationAttributes(model);
            
            // Cerchiamo l'utente dal repository
            Users seller = usersService.findByUsername(username);
            
            // Se l'utente non esiste, redirect alla homepage
            if (seller == null) {
                return "redirect:/";
            }
            
            // Carica i prodotti dell'utente
            List<Product> sellerProducts = productRepository.findByAutoreWithImages(seller);
            
            model.addAttribute("sellerUsername", username);
            model.addAttribute("sellerProducts", sellerProducts);
            
            return "seller-profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Impossibile caricare il profilo del venditore. Riprova più tardi.");
            return "error";
        }
    }
}