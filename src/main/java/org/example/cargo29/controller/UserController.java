package org.example.cargo29.controller;

import org.example.cargo29.entity.User;
import org.example.cargo29.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Получение баланса пользователя
    @GetMapping("/{id}/balances")
    public ResponseEntity<Map<String, Double>> getUserBalances(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getBalances());
    }

    // Пополнение баланса пользователя
    @PostMapping("/{id}/balance")
    public ResponseEntity<String> updateBalance(@PathVariable Long id,
                                                @RequestParam String currency,
                                                @RequestParam Double amount) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.updateBalance(user, currency, amount);
        return ResponseEntity.ok("Баланс обновлён");
    }
}
