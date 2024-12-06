package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.example.exchangeP2P.service.CurrencyService;
import org.example.exchangeP2P.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class PayController {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public String depositBalance(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }

        // Получаем список всех валют
        List<Currency> currencies = currencyService.listAll();

        // Получаем балансы пользователя по каждой валюте
        model.addAttribute("user", user);
        model.addAttribute("currencies", currencies);
        model.addAttribute("userBalances", user.getBalances());

        return "add_balance";
    }

    @PostMapping("/balance/deposit")
    public String depositBalance(@RequestParam Long sourceCurrencyId, @AuthenticationPrincipal User user, @RequestParam double amount, Model model) {
        Currency currency = currencyRepository.findById(sourceCurrencyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid source currency"));

        // Добавляем средства на баланс пользователя
        userService.addFunds(user, currency, amount);

        model.addAttribute("message", "Баланс успешно пополнен!");
        return "redirect:/";
    }
}
