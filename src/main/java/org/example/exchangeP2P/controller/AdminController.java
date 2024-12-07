package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.RoleRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Страница управления пользователями
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    // Логика переключения ролей
    @PostMapping("/users/{id}/toggleRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleRole(@PathVariable("id") Long id, @RequestParam("roleName") String roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + roleName));
        Set<Role> roles = new HashSet<>();
        if ("ADMIN".equals(roleName)) {
            roles.add(role);
        } else {
            roles.add(roleRepository.findByName("USER").orElseThrow(() -> new IllegalArgumentException("Role USER not found")));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // Страница управления валютами
    @GetMapping("/currencies")
    @PreAuthorize("hasRole('ADMIN')")
    public String listCurrencies(Model model) {
        List<Currency> currencies = currencyRepository.findAll();
        model.addAttribute("currencies", currencies);
        return "admin_currencies";
    }

    @PostMapping("/currency/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCurrency(@ModelAttribute Currency currency) {
        currencyRepository.save(currency);
        return "redirect:/admin/currencies";
    }

    @GetMapping("/currency/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCurrency(@PathVariable Long id, Model model) {
        Currency currency = currencyRepository.findById(id).orElseThrow();
        model.addAttribute("currency", currency);
        return "admin_edit_currency";
    }

    @PostMapping("/currency/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCurrency(@PathVariable Long id, @ModelAttribute Currency currency) {
        currency.setId(id);
        currencyRepository.save(currency);
        return "redirect:/admin/currencies";
    }

    @DeleteMapping("/currency/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCurrency(@PathVariable Long id) {
        currencyRepository.deleteById(id);
        return "redirect:/admin/currencies";
    }

    // Страница управления ордерами
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "admin_orders";
    }

    @PostMapping("/order/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String activateOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus("ACTIVE");
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }

    @PostMapping("/order/deactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus("INACTIVE");
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }
}
