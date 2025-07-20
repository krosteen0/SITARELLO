package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.CartItem;
import org.springframework.data.repository.CrudRepository;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {
}
