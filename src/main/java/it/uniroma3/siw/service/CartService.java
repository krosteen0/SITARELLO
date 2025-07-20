package it.uniroma3.siw.service;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(Users user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart(user);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public void addProductToCart(Users user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) return;
        Product product = productOpt.get();
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                return;
            }
        }
        CartItem newItem = new CartItem(cart, product, quantity);
        cart.getItems().add(newItem);
        cartItemRepository.save(newItem);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeProductFromCart(Users user, Long productId) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().removeIf(item -> {
            boolean match = item.getProduct().getId().equals(productId);
            if (match) cartItemRepository.delete(item);
            return match;
        });
        cartRepository.save(cart);
    }

    @Transactional
    public void updateQuantity(Users user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
                break;
            }
        }
    }

    public List<CartItem> getCartItems(Users user) {
        Cart cart = getOrCreateCart(user);
        return cart.getItems();
    }

    public void clearCart(Users user) {
        Cart cart = getOrCreateCart(user);
        for (CartItem item : cart.getItems()) {
            cartItemRepository.delete(item);
        }
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
