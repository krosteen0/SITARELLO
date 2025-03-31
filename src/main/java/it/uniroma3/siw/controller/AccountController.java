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
        // Controlla se le password coincidono
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Le password non coincidono");
            return "registration";
        }

        // Controlla se l'email è già registrata
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email già registrata");
            return "registration";
        }

        // Salva l'utente
        userService.saveUser(user);

        // Aggiungi un messaggio di conferma
        model.addAttribute("success", "Registrazione completata con successo! Ora puoi accedere.");
        return "login"; // Mostra la pagina di login con il messaggio di successo
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Ritorna la vista login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        // Verifica le credenziali nel database
        User user = userService.findByEmail(email);
        if (user == null || !userService.checkPassword(user, password)) {
            model.addAttribute("error", "Credenziali non valide");
            return "login"; // Ritorna alla pagina di login con un messaggio di errore
        }

        // Se il login ha successo, reindirizza alla homepage
        return "redirect:/";
    }
}