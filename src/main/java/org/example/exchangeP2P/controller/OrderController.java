package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.OrderRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }



    @GetMapping("/create")
    public String createOrderForm() {
        return "orders";
    }

    @PostMapping
    public String createOrder(@AuthenticationPrincipal  User user,
                              @RequestParam String sourceCurrency,
                              @RequestParam double amount,
                              @RequestParam String targetCurrency,
                              @RequestParam double price) {

        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }

        Order order = new Order();
        order.setSourceCurrency(sourceCurrency);
        order.setAmount(amount);
        order.setTargetCurrency(targetCurrency);
        order.setPrice(price);
        order.setUser(user);
        orderRepository.save(order);
        return "redirect:/";
    }
}
