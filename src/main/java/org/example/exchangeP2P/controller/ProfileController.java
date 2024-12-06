package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/profile")
    public String viewAuthorPage(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }

        model.addAttribute("user", user);
        model.addAttribute("balances", user.getBalances()); // Замените на правильное имя метода для получения балансов пользователя

        return "user_profile";
    }

}
