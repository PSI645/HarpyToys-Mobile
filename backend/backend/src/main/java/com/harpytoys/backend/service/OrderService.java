package com.harpytoys.backend.service;

import com.harpytoys.backend.model.CartItem;
import com.harpytoys.backend.model.Order;
import com.harpytoys.backend.model.User;
import com.harpytoys.backend.repository.CartRepository;
import com.harpytoys.backend.repository.OrderRepository;
import com.harpytoys.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order checkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<CartItem> items = cartRepository.findByUserId(userId);

        if (items.isEmpty()) {
            throw new RuntimeException("Carrinho vazio");
        }

        double total = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus("CONFIRMADO");

        Order saved = orderRepository.save(order);

        cartRepository.deleteAll(items);

        return saved;
    }

    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}