package com.harpytoys.backend.service;

import com.harpytoys.backend.model.CartItem;
import com.harpytoys.backend.model.Product;
import com.harpytoys.backend.model.User;
import com.harpytoys.backend.repository.CartRepository;
import com.harpytoys.backend.repository.ProductRepository;
import com.harpytoys.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public CartItem addToCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Optional<CartItem> existing = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            return cartRepository.save(item);
        }

        CartItem newItem = new CartItem();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setQuantity(1);
        return cartRepository.save(newItem);
    }

    public void removeFromCart(Long userId, Long itemId) {
        cartRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
    }
}