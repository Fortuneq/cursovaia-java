package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    private final CurrencyRepository currencyRepository;

    public OrderController(OrderRepository orderRepository, CurrencyRepository currencyRepository) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
    }


//    @GetMapping("/by_user")
//    public String getUserOrders(@AuthenticationPrincipal User user,Model model) {
//
//        List<Order> orders = orderRepository.findByUserId(user.getId());
//        List<Currency> currencies = currencyRepository.findAll();
//        model.addAttribute("currencies", currencies);
//        return "orders";
//    }



    @GetMapping("/create")
    public String createOrderForm(Model model) {
        List<Currency> currencies = currencyRepository.findAll();
        model.addAttribute("currencies", currencies);
        return "orders";
    }

    @PostMapping
    public String createOrder(@AuthenticationPrincipal User user,
                              @RequestParam Long sourceCurrencyId,
                              @RequestParam Long targetCurrencyId,
                              @RequestParam double amount,
                              @RequestParam double price) {
        Currency sourceCurrency = currencyRepository.findById(sourceCurrencyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid source currency"));
        Currency targetCurrency = currencyRepository.findById(targetCurrencyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid target currency"));

        Order order = new Order();
        order.setUser(user);
        order.setSourceCurrency(sourceCurrency);
        order.setTargetCurrency(targetCurrency);
        order.setAmount(amount);
        order.setPrice(price);
        orderRepository.save(order);

        return "redirect:/";
    }
}
