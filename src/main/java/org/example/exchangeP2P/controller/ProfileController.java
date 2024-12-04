package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("exchangeRequests", user.getExchangeRequests());
        model.addAttribute("currencies", new String[]{"USD", "EUR", "GBP", "JPY"}); // Добавление доступных валют
        return "profile";  // Страница профиля
    }
}
