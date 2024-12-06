package org.example.exchangeP2P.service;


import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        // Реализуйте логику получения текущего пользователя
        return userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public void addFunds(User user, Currency currency, double amount) {
        Hibernate.initialize(user.getBalances());
        user.addFunds(currency, amount);
        userRepository.save(user);
    }
}
