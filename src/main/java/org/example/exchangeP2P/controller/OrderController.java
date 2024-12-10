package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository, CurrencyRepository currencyRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
    }

    // Получить ордера текущего пользователя
    @GetMapping("/user")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User currentUser,
                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "sort", defaultValue = "asc") String sort) {
        List<Order> orders;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));


        orders = orderRepository.findByUser(user);

        return ResponseEntity.ok(orders);
    }


    @GetMapping//массив ордеров
    public ResponseEntity<List<Order>> getAllOrders(@AuthenticationPrincipal User currentUser,
                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "sort", defaultValue = "asc") String sort) {
        List<Order> orders;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        orders =  orderRepository.findAll();

        return ResponseEntity.ok(orders);
    }


    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order orderRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));


        // Получаем исходную валюту
        Currency sourceCurrency = currencyRepository.findById(orderRequest.getSourceCurrency().getId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная исходная валюта"));

        // Получаем целевую валюту
        Currency targetCurrency = currencyRepository.findById(orderRequest.getTargetCurrency().getId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная целевая валюта"));

        // Создаем новый ордер
        Order order = new Order();
        order.setUser(user);
        order.setSourceCurrency(sourceCurrency);
        order.setTargetCurrency(targetCurrency);
        order.setAmount(orderRequest.getAmount());
        order.setPrice(orderRequest.getPrice());

        // Сохраняем ордер в базу данных
        orderRepository.save(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(currencies);
    }
}
