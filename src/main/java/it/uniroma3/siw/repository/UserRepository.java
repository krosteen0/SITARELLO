package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Users findByEmail(String email);

    Users findByUsername(String username);
}