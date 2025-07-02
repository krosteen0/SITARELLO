package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.dto.UsersDTO;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.UsersRepository;

@Service
public class UsersService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users saveUser(UsersDTO usersDTO) {
        Users user = new Users();
        user.setUsername(usersDTO.getUsername());
        user.setEmail(usersDTO.getEmail());
        user.setPassword(passwordEncoder.encode(usersDTO.getPassword()));
        user.setRole("USER");
        return usersRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
    }
    
    /**
     * Trova un utente dal suo username
     * @param username username dell'utente da cercare
     * @return l'oggetto Users o null se non trovato
     */
    public Users findByUsername(String username) {
        return usersRepository.findByUsername(username).orElse(null);
    }
}