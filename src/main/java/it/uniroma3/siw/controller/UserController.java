package it.uniroma3.siw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
    public String registerUser(@ModelAttribute @Valid Users user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            Users existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null) {
                model.addAttribute("errorMessage", "Email già registrata");
                return "registration";
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
        model.addAttribute("user", new Users());
        return "login";
    }

    // Gestisce il login degli utenti
    @PostMapping("/login")
    public String loginUser(@ModelAttribute Users user, Model model, HttpSession session) {
        logger.info("Tentativo di login con username o email: {}", user.getUsernameOrEmail());

        Users existingUser = userService.findByUsernameOrEmail(user.getUsernameOrEmail());
        if (existingUser == null) {
            model.addAttribute("errorMessage", "Utente non trovato");
            return "login";
        }

        if (!userService.checkPassword(existingUser, user.getPassword())) {
            model.addAttribute("errorMessage", "Password non valida");
            return "login";
        }

        session.setAttribute("loggedUser", existingUser);
        return "redirect:/";
    }

    // Mostra il form delle impostazioni
    @GetMapping("/settings")
    public String showSettingsForm(Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("users", loggedUser);
        return "settings";
    }

    // Gestisce l'aggiornamento delle impostazioni
    @PostMapping("/settings")
    public String updateSettings(@ModelAttribute @Valid Users user, BindingResult bindingResult, 
                                Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "settings";
        }

        try {
            // Verifica se l'email è già in uso da un altro utente
            Users existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null && !existingUser.getId().equals(loggedUser.getId())) {
                model.addAttribute("errorMessage", "Email già registrata");
                return "settings";
            }

            // Verifica se lo username è già in uso da un altro utente
            existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null && !existingUser.getId().equals(loggedUser.getId())) {
                model.addAttribute("errorMessage", "Username già in uso");
                return "settings";
            }

            // Verifica la password (se fornita)
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                if (!user.getPassword().equals(user.getConfirmPassword())) {
                    model.addAttribute("errorMessage", "Le password non corrispondono");
                    return "settings";
                }
            }

            // Aggiorna i dati dell'utente
            loggedUser.setUsername(user.getUsername());
            loggedUser.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                loggedUser.setPassword(user.getPassword());
            }

            // Salva le modifiche
            userService.updateUser(loggedUser);
            session.setAttribute("loggedUser", loggedUser); // Aggiorna la sessione
            model.addAttribute("successMessage", "Impostazioni aggiornate con successo!");
            return "settings";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "settings";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}