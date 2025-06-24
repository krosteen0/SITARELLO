package it.uniroma3.siw.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.ChangePasswordDTO;
import it.uniroma3.siw.model.LoginDTO;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.UserSettingsDTO;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

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
            logger.warn("Errori di validazione durante la registrazione: {}", bindingResult.getAllErrors());
            return "registration";
        }
        try {
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("errorMessage", "Email già registrata");
                return "registration";
            }
            userService.saveUser(user);
            model.addAttribute("successMessage", "Registrazione completata con successo! Ora puoi accedere.");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Errore durante la registrazione: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "registration";
        }
    }

    // Mostra il modulo di login
    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("user", new LoginDTO());
        if (error != null) {
            model.addAttribute("errorMessage", "Credenziali non valide");
            logger.warn("Errore di login rilevato: {}", error);
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Logout effettuato con successo");
            logger.info("Logout completato");
        }
        return "login";
    }

    // Gestisce il login degli utenti
    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") LoginDTO loginDTO, Model model, HttpSession session) {
        logger.info("Tentativo di login con username o email: {}", loginDTO.getUsernameOrEmail());

        Users existingUser = userService.findByUsernameOrEmail(loginDTO.getUsernameOrEmail());
        if (existingUser == null) {
            logger.warn("Utente non trovato: {}", loginDTO.getUsernameOrEmail());
            model.addAttribute("errorMessage", "Utente non trovato");
            model.addAttribute("user", new LoginDTO());
            return "login";
        }

        logger.info("Utente trovato: username={}, email={}", existingUser.getUsername(), existingUser.getEmail());
        boolean passwordValid = userService.checkPassword(existingUser, loginDTO.getPassword());
        logger.info("Password valida: {}", passwordValid);
        if (!passwordValid) {
            logger.warn("Password non valida per utente: {}", loginDTO.getUsernameOrEmail());
            model.addAttribute("errorMessage", "Password non valida");
            model.addAttribute("user", new LoginDTO());
            return "login";
        }

        session.setAttribute("loggedUser", existingUser);
        logger.info("Sessione aggiornata: loggedUser={}", existingUser.getUsername());
        logger.info("Reindirizzamento a /my-products per utente: {}", existingUser.getUsername());
        return "redirect:/my-products";
    }

    // Mostra il form delle impostazioni
    @GetMapping("/editProfile")
    public String showSettingsForm(Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Nessun utente loggato, reindirizzamento a /login");
            return "redirect:/login";
        }
        UserSettingsDTO userSettingsDTO = new UserSettingsDTO();
        userSettingsDTO.setUsername(loggedUser.getUsername());
        model.addAttribute("userSettingsDTO", userSettingsDTO);
        logger.info("Caricato DTO per utente: username={}", userSettingsDTO.getUsername());
        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String updateSettings(@ModelAttribute @Valid UserSettingsDTO userSettingsDTO, BindingResult bindingResult, 
                                Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Nessun utente loggato, reindirizzamento a /login");
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            logger.error("Errori di validazione: {}", bindingResult.getAllErrors());
            return "editProfile";
        }

        try {
            // Verifica se lo username è già in uso
            if (!userSettingsDTO.getUsername().equals(loggedUser.getUsername())) {
                if (userService.existsByUsername(userSettingsDTO.getUsername())) {
                    model.addAttribute("errorMessage", "Username già in uso");
                    return "editProfile";
                }
            }

            // Aggiorna i dati
            loggedUser.setUsername(userSettingsDTO.getUsername());

            // Salva le modifiche nel database
            Users updatedUser = userService.updateUser(loggedUser);
            logger.info("Utente aggiornato: username={}", updatedUser.getUsername());

            // Aggiorna la sessione
            session.setAttribute("loggedUser", updatedUser);
            model.addAttribute("successMessage", "Impostazioni aggiornate con successo!");
            return "editProfile";
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento: " + e.getMessage());
            return "editProfile";
        }
    }

    @GetMapping("/editPassword")
    public String showChangePasswordForm(Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Nessun utente loggato, reindirizzamento a /login");
            return "redirect:/login";
        }
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        logger.info("Caricato form di cambio password per utente: {}", loggedUser.getUsername());
        return "editPassword";
    }

    @PostMapping("/editPassword")
public String changePassword(@ModelAttribute @Valid ChangePasswordDTO changePasswordDTO, BindingResult bindingResult, 
                            Model model, HttpSession session) {
    Users loggedUser = (Users) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        logger.warn("Nessun utente loggato, reindirizzamento a /login");
        return "redirect:/login";
    }

    if (bindingResult.hasErrors()) {
        logger.error("Errori di validazione: {}", bindingResult.getAllErrors());
        return "editPassword";
    }

    try {
        // Verifica la password attuale
        logger.debug("Verifica della password attuale per utente: {}", loggedUser.getUsername());
        logger.debug("Password attuale nel DB: {}, Password inserita: {}", 
                    loggedUser.getPassword() != null ? "[protected]" : "null", 
                    changePasswordDTO.getCurrentPassword() != null ? "[provided]" : "null");
                    
        if (!userService.checkPassword(loggedUser, changePasswordDTO.getCurrentPassword())) {
            logger.warn("Password attuale errata per utente: {}", loggedUser.getUsername());
            model.addAttribute("errorMessage", "Password Attuale Errata");
            return "editPassword";
        }

        // Verifica che la nuova password e la conferma corrispondano
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            logger.warn("Le nuove password non corrispondono per utente: {}", loggedUser.getUsername());
            model.addAttribute("errorMessage", "Le nuove password non corrispondono");
            return "editPassword";
        }

        // Verifica che la nuova password sia diversa dalla password attuale
        if (userService.checkPassword(loggedUser, changePasswordDTO.getNewPassword())) {
            logger.warn("La nuova password è uguale alla password attuale per utente: {}", loggedUser.getUsername());
            model.addAttribute("errorMessage", "La nuova password deve essere diversa da quella attuale");
            return "editPassword";
        }

        // Esegui l'aggiornamento solo se tutti i controlli sono superati
        logger.info("Aggiornamento della password per utente: {}", loggedUser.getUsername());
        logger.debug("Nuova password da impostare: {}", 
                    changePasswordDTO.getNewPassword() != null ? "[provided]" : "null");
        loggedUser.setPassword(changePasswordDTO.getNewPassword());
        Users updatedUser = userService.updateUser(loggedUser);
        logger.info("Password aggiornata con successo per utente: username={}", updatedUser.getUsername());

        // Aggiorna la sessione
        session.setAttribute("loggedUser", updatedUser);
        model.addAttribute("successMessage", "Password aggiornata con successo!");
        return "editPassword";
    } catch (Exception e) {
        logger.error("Errore durante il cambio password per utente {}: {}", 
                     loggedUser != null ? loggedUser.getUsername() : "null", e.getMessage(), e);
        model.addAttribute("errorMessage", "Errore durante il cambio password: " + e.getMessage());
        return "editPassword";
    }
}

    // Gestisce il logout
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        logger.info("Sessione invalidata per logout");
        return "redirect:/login?logout";
    }

    // Mostra la pagina del profilo
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Nessun utente loggato, reindirizzamento a /login");
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        logger.info("Caricato profilo per utente: username={}, email={}", 
                    loggedUser.getUsername(), loggedUser.getEmail());
        return "profile";
    }

    // Gestisce l'aggiornamento delle impostazioni dal profilo
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute @Valid UserSettingsDTO userSettingsDTO, BindingResult bindingResult, 
                               Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Nessun utente loggato, reindirizzamento a /login");
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            logger.error("Errori di validazione: {}", bindingResult.getAllErrors());
            model.addAttribute("loggedUser", loggedUser);
            return "profile";
        }

        try {
            // Verifica se lo username è già in uso
            if (!userSettingsDTO.getUsername().equals(loggedUser.getUsername())) {
                if (userService.existsByUsername(userSettingsDTO.getUsername())) {
                    model.addAttribute("errorMessage", "Username già in uso");
                    model.addAttribute("loggedUser", loggedUser);
                    return "profile";
                }
            }

            // Aggiorna i dati
            loggedUser.setUsername(userSettingsDTO.getUsername());

            // Salva le modifiche
            Users updatedUser = userService.updateUser(loggedUser);
            logger.info("Utente aggiornato: username={}", updatedUser.getUsername());

            // Aggiorna la sessione
            session.setAttribute("loggedUser", updatedUser);
            model.addAttribute("successMessage", "Impostazioni aggiornate con successo!");
            model.addAttribute("loggedUser", updatedUser);
            return "redirect:/profile";
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("loggedUser", loggedUser);
            return "profile";
        }
    }

    @GetMapping("/publicProfile/{username}")
    public String showPublicProfile(@PathVariable String username, Model model) {
        // Cerca l'utente per username
        Users user = userService.findByUsername(username);

        if (user == null) {
            // Se l'utente non esiste, aggiungi un attributo per il messaggio di errore
            model.addAttribute("user", null);
            return "publicProfile"; // Il template gestirà il caso user == null
        }

        // Recupera i prodotti dell'utente
        List<Product> products = productService.findProductsByAutore(user.getUsername());

        // Aggiungi i dati al modello per il template
        model.addAttribute("user", user);
        model.addAttribute("products", products);

        return "publicProfile";
    }
}