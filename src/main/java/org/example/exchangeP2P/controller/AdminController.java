package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.BalanceRepository;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.example.exchangeP2P.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final OrderRepository orderRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    private final BalanceService balanceService;

    private final BalanceRepository balanceRepository;

    @Autowired
    public AdminController(OrderRepository orderRepository, CurrencyRepository currencyRepository, UserRepository userRepository, BalanceService balanceService, BalanceRepository balanceRepository) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.balanceService = balanceService;
        this.balanceRepository = balanceRepository;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(currencies);
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

}