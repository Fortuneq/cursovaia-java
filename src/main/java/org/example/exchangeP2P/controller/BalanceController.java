package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/balance")
public class BalanceController {

    private final UserService userService;

    @Autowired
    public BalanceController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/top-up")
    public String topUpPage(Model model) {
        // Список доступных валют
        model.addAttribute("currencies", new String[]{"USD", "EUR", "GBP", "JPY"});
        return "top_up_balance";  // Страница пополнения баланса
    }

    @PostMapping("/top-up")
    public String topUpBalance(@RequestParam String currency, @RequestParam Double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        userService.updateBalance(user, currency, amount);
        return "redirect:/profile";  // Перенаправление на профиль
    }
}