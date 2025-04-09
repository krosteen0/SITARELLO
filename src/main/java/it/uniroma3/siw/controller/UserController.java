// src/main/java/it/uniroma3/siwprogetto/controller/UserController.java
package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("users", new Users());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute Users user, Model model) {
        try {
            Users existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null){
                return "registration"; // Torna alla pagina di registrazione con un messaggio di errore
            }
            userService.saveUser(user);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registration";
        }
    }

    @Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new Users()); // Aggiunge un oggetto vuoto al modello
        return "login"; // Ritorna il template login.html
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") Users user, Model model) {
        System.out.println("Login attempt with email: " + user.getEmail());
        // Verifica se l'utente esiste nel database
        Users existingUser = userService.findByEmail(user.getEmail());
        if (existingUser == null || !userService.checkPassword(existingUser, user.getPassword())) {
            model.addAttribute("errorMessage", "Email o password non validi");
            return "redirect:/"; // Torna alla pagina di login con un messaggio di errore
        }

        // Se il login ha successo, reindirizza alla homepage
        return "redirect:/";
    }
}
}