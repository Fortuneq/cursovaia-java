package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.entity.UserBalances;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.RoleRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("user", new User());
        return "login"; // Используем один шаблон для регистрации и входа
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/register")
    public @ResponseBody String registerUser(@Valid @ModelAttribute("user") User user,
                                             BindingResult result,
                                             Model model){
        if(result.hasErrors()){
            return "Ошибка регистрации: " + result.getAllErrors().toString();
        }
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return "Имя пользователя уже существует";
        }
        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Назначение роли "USER" по умолчанию
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        user = userRepository.save(user);

        List<Currency> currencies = currencyRepository.findAll(); // Получить список всех валют
        Set<UserBalances> userBalances = new HashSet<>();
        for (Currency currency : currencies) {
            UserBalances balance = new UserBalances();
            balance.setUser(user);
            balance.setCurrency(currency);
            balance.setBalance(0); // Баланс по умолчанию
            userBalances.add(balance);
        }
        user.setBalances(userBalances);
        userRepository.save(user);
        return "Регистрация прошла успешно!";
    }
}