package org.example.cargo29.controller;

import org.example.cargo29.entity.ExchangeRequest;
import org.example.cargo29.entity.User;
import org.example.cargo29.service.ExchangeService;
import org.example.cargo29.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final UserService userService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService, UserService userService) {
        this.exchangeService = exchangeService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        // Список доступных валют
        model.addAttribute("currencies", new String[]{"USD", "EUR", "GBP", "JPY"});
        model.addAttribute("order", new ExchangeRequest());
        return "create_order";  // Страница создания ордера
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute ExchangeRequest order) {
        exchangeService.createRequest(order);
        return "redirect:/profile";  // Перенаправление на профиль
    }
}
