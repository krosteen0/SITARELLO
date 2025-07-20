package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.model.CartItem;
import it.uniroma3.siw.service.CartService;
import it.uniroma3.siw.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UsersRepository usersRepository;

    private Users getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return usersRepository.findByUsername(auth.getName()).orElse(null);
        }
        return null;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        Users user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).body("Utente non autenticato");
        cartService.addProductToCart(user, productId, quantity);
        return ResponseEntity.ok().body("Aggiunto al carrello");
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long productId) {
        Users user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).body("Utente non autenticato");
        cartService.removeProductFromCart(user, productId);
        return ResponseEntity.ok().body("Rimosso dal carrello");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        Users user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).body("Utente non autenticato");
        cartService.updateQuantity(user, productId, quantity);
        return ResponseEntity.ok().body("Quantità aggiornata");
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems() {
        Users user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).body("Utente non autenticato");
        List<CartItem> items = cartService.getCartItems(user);
        // Restituisci solo i dati essenziali per il frontend
        return ResponseEntity.ok(items.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("productId", item.getProduct().getId());
            map.put("name", item.getProduct().getName());
            map.put("price", item.getProduct().getPrice());
            map.put("quantity", item.getQuantity());
            map.put("imageId", item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0).getId());
            return map;
        }));
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart() {
        Users user = getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).body("Utente non autenticato");
        cartService.clearCart(user);
        return ResponseEntity.ok().body("Carrello svuotato");
    }
}
