// src/main/java/it/uniroma3/siwprogetto/controller/UserController.java
package it.uniroma3.siw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Mostra il modulo di registrazione
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("users", new Users());
        return "registration";
    }

    // Gestisce la registrazione degli utenti
    @PostMapping("/registration")
    public String registerUser(@ModelAttribute Users user, Model model) {
        try {
            Users existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null) {
                model.addAttribute("errorMessage", "Email già registrata");
                return "registration"; // Torna alla pagina di registrazione con un messaggio di errore
            }
            userService.saveUser(user);
            model.addAttribute("successMessage", "Registrazione completata con successo! Ora puoi accedere.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registration";
        }
    }

    // Mostra il modulo di login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new Users()); // Aggiunge un oggetto vuoto al modello
        return "login"; // Ritorna il template login.html
    }

    // Gestisce il login degli utenti
    @PostMapping("/login")
    public String loginUser(@ModelAttribute Users user, Model model, HttpSession session) {
        logger.info("Tentativo di login con username o email: {}", user.getUsernameOrEmail());

        // Cerca l'utente per username o email
        Users existingUser = userService.findByUsernameOrEmail(user.getUsernameOrEmail());
        if (existingUser == null) {
            model.addAttribute("errorMessage", "Utente non trovato");
            return "login"; // Torna alla pagina di login con un messaggio di errore
        }

        // Verifica la password
        if (!userService.checkPassword(existingUser, user.getPassword())) {
            model.addAttribute("errorMessage", "Password non valida");
            return "login"; // Torna alla pagina di login con un messaggio di errore
        }

        // Salva l'utente nella sessione
        session.setAttribute("loggedUser", existingUser);

        // Reindirizza alla homepage
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalida la sessione
        return "redirect:/"; // Reindirizza alla homepage
    }
}