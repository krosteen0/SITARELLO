package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User()); // Aggiunge un oggetto User vuoto al modello
        return "registration"; // Ritorna il nome della vista (registration.html)
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute User user, Model model) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Le password non coincidono");
            return "registration";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Ritorna la vista login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        // Logica di autenticazione (da implementare)
        // Esempio: verifica email e password nel database
        if (email.equals("test@example.com") && password.equals("password")) {
            return "redirect:/"; // Reindirizza alla homepage se il login ha successo
        }
        model.addAttribute("error", "Credenziali non valide");
        return "login"; // Ritorna alla pagina di login con un messaggio di errore
    }
}