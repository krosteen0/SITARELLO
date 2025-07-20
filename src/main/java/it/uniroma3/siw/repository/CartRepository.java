package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Cart;
import it.uniroma3.siw.model.Users;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {
    Optional<Cart> findByUser(Users user);
}
