package it.uniroma3.siw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public Users saveUser(Users user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            logger.info("Codifica della nuova password per utente: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            logger.warn("Nessuna password fornita per utente: {}", user.getUsername());
            throw new IllegalArgumentException("La password è obbligatoria per un nuovo utente");
        }
        logger.info("Tentativo di salvare nuovo utente: username={}", user.getUsername());
        Users savedUser = userRepository.save(user);
        logger.info("Utente salvato: username={}", savedUser.getUsername());
        return savedUser;
    }
    
    @Transactional
    public Users updateUser(Users user) {
        // Carica l'entità esistente dal database per mantenere la password attuale
        Users existingUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + user.getUsername()));
        
        // Aggiorna la password solo se è stata fornita una nuova password valida
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            logger.info("Codifica della nuova password per utente: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            logger.debug("Nessuna modifica alla password per utente: {}", user.getUsername());
            user.setPassword(existingUser.getPassword()); // Mantieni la password esistente
        }
        
        logger.info("Tentativo di aggiornare utente: username={}", user.getUsername());
        Users savedUser = userRepository.save(user);
        logger.info("Utente aggiornato: username={}, password: [protected]", savedUser.getUsername());
        return savedUser;
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public Users findByEmail(String email) {
        Users user = userRepository.findByEmail(email);
        if (user != null) {
            // Calcola la media delle recensioni dei prodotti
            Double averageRating = userRepository.findAverageRatingByAutore(user.getId());
            user.setAverageRating(averageRating != null ? averageRating : 0.0);
        }
        return user;
    }
    
    public Users findByUsername(String username) {
        Users user = userRepository.findByUsername(username);
        if (user != null) {
            // Calcola la media delle recensioni dei prodotti
            Double averageRating = userRepository.findAverageRatingByAutore(user.getId());
            user.setAverageRating(averageRating != null ? averageRating : 0.0);
        }
        return user;
    }
    
    public Users findByUsernameOrEmail(String usernameOrEmail) {
        Users user = userRepository.findByUsernameOrEmail(usernameOrEmail);
        if (user != null) {
            Double averageRating = userRepository.findAverageRatingByAutore(user.getId());
            user.setAverageRating(averageRating != null ? averageRating : 0.0);
        }
        return user;
    }
    
    
    public boolean checkPassword(Users user, String rawPassword) {
        if (user == null || user.getPassword() == null || rawPassword == null) {
            logger.warn("Parametri non validi per checkPassword: user={}, rawPassword={}", 
            user != null ? user.getUsername() : "null", rawPassword != null ? "[provided]" : "null");
            return false;
        }
        try {
            logger.debug("Verifica password per utente: {}", user.getUsername());
            boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
            logger.debug("Risultato verifica password per utente {}: {}", user.getUsername(), matches);
            return matches;
        } catch (Exception e) {
            logger.error("Errore durante la verifica della password per utente {}: {}", 
            user.getUsername(), e.getMessage(), e);
            return false;
        }
    }
}