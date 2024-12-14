package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.*;
import org.example.exchangeP2P.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final OrderRepository orderRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    private final BalanceService balanceService;

    private final BalanceRepository balanceRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(OrderRepository orderRepository, CurrencyRepository currencyRepository, UserRepository userRepository, BalanceService balanceService, BalanceRepository balanceRepository, RoleRepository roleRepository) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.balanceService = balanceService;
        this.balanceRepository = balanceRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/orders")//массив заявок
    public ResponseEntity<List<Order>> getAllOrders(@AuthenticationPrincipal User currentUser,
                                                    @RequestParam(value = "keyword", required = false) String keyword,
                                                    @RequestParam(value = "sort", defaultValue = "asc") String sort) {
        List<Order> orders;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = user.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        orders =  orderRepository.findAll();

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orders/{orderId}/{action}")
    public ResponseEntity<String> toggleOrderStatus(@PathVariable Long orderId, @PathVariable String action) {
        // Проверяем, существует ли заявка
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заявка не найдена");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = user.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Order order = optionalOrder.get();

        // Обновляем статус на основе действия
        if ("activate".equalsIgnoreCase(action)) {
            order.setStatus("ACTIVE");
        } else if ("deactivate".equalsIgnoreCase(action)) {
            order.setStatus("INACTIVE");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректное действие");
        }

        // Сохраняем изменения
        orderRepository.save(order);

        return ResponseEntity.ok("Статус заявки обновлен");
    }




        // Получение списка всех валют
        @GetMapping("/currencies")
        public ResponseEntity<List<Currency>> getAllCurrencies() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            Set<Role> roles = user.getRoles();

            boolean hasAdminRole = roles.stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));

            if (!hasAdminRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            List<Currency> currencies = currencyRepository.findAll();
            return ResponseEntity.ok(currencies);
        }

        // Добавление новой валюты
        @PostMapping("/currencies")
        public ResponseEntity<String> addCurrency(@RequestBody Currency currency) {
            if (currency.getCode() == null || currency.getName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Код и название валюты обязательны");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            Set<Role> roles = user.getRoles();

            boolean hasAdminRole = roles.stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));

            if (!hasAdminRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            currencyRepository.save(currency);
            return ResponseEntity.status(HttpStatus.CREATED).body("Валюта добавлена");
        }

        // Удаление валюты
        @DeleteMapping("/currencies/{id}")
        public ResponseEntity<String> deleteCurrency(@PathVariable Long id) {
            Optional<Currency> currency = currencyRepository.findById(id);
            if (currency.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Валюта не найдена");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            Set<Role> roles = user.getRoles();

            boolean hasAdminRole = roles.stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));

            if (!hasAdminRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            currencyRepository.deleteById(id);
            return ResponseEntity.ok("Валюта удалена");
        }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = user.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Добавление нового пользователя
    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User admin = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = admin.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Имя пользователя и пароль обязательны");
        }

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь добавлен");
    }

    // Удаление пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User admin = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = admin.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Пользователь удален");
    }

    // Назначить пользователя администратором
    @PostMapping("/users/{id}/make-admin")
    public ResponseEntity<String> makeAdmin(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User admin = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = admin.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
        Role role =  roleRepository.findByName("ADMIN").get();
        user.get().getRoles().add(role);
        userRepository.save(user.get());
        return ResponseEntity.ok("Пользователь стал администратором");
    }

    // Снять права администратора с пользователя
    @PostMapping("/users/{id}/remove-admin")
    public ResponseEntity<String> removeAdmin(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User admin = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Set<Role> roles = admin.getRoles();

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
        user.get().getRoles().removeIf(role -> role.getName().equals("ADMIN"));
        userRepository.save(user.get());
        return ResponseEntity.ok("Права администратора сняты");
    }

}