package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    private final CurrencyRepository currencyRepository;

    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, CurrencyRepository currencyRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/by_user")
    public String userOrders(@RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "sort", defaultValue = "asc") String sort,
                             Model model) {

        Sort sortOrder;

        if ("desc".equals(sort)) {
            sortOrder = Sort.by(Sort.Order.desc("price"));
        } else {
            sortOrder = Sort.by(Sort.Order.asc("price"));
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Поиск пользователя в базе данных
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + currentUsername));


        // Поиск ордеров пользователя с фильтром по ключевому слову (если нужно)
        List<Order> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = orderRepository.findByUserAndKeyword(currentUser, keyword, sortOrder);
        } else {
            orders = orderRepository.findByUser(currentUser, sortOrder);
        }

        model.addAttribute("listOrder", orders);
        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "user_orders";
    }


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
