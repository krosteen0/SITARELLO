package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Users saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Crittografa la password
        return userRepository.save(user); // Salva l'utente nel database
    }

    // Verifica se un'email è già registrata
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Trova un utente tramite email
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Verifica se la password fornita corrisponde a quella salvata
    public boolean checkPassword(Users user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}

