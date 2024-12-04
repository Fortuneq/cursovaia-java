package org.example.cargo29.service;

import org.example.cargo29.entity.User;
import org.example.cargo29.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Поиск пользователя по ID
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Поиск пользователя по ID
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Обновление баланса пользователя
    public void updateBalance(User user, String currency, Double amount) {
        Map<String, Double> balances = user.getBalances();
        balances.put(currency, balances.getOrDefault(currency, 0.0) + amount);
        user.setBalances(balances);
        userRepository.save(user);
    }


    // Другие методы
}
