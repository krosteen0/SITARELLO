package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.dto.UsersDTO;
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
        addAuthenticationAttributes(model);
        Users authenticatedUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", authenticatedUser);
        return "profile";
    }
    
    @GetMapping("/products")
    @Transactional(readOnly = true)
    public String showUserProducts(Model model) {
        try {
            addAuthenticationAttributes(model);
            Users authenticatedUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("products", productRepository.findByAutoreWithImages(authenticatedUser));
            model.addAttribute("user", authenticatedUser);
            return "products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Impossibile caricare i tuoi prodotti. Riprova più tardi.");
            return "error";
        }
    }
}