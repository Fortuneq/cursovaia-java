package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Balance;
import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.BalanceRepository;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final OrderRepository orderRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final BalanceRepository balanceRepository;

    @Autowired
    public BalanceController(OrderRepository orderRepository, CurrencyRepository currencyRepository, UserRepository userRepository, BalanceRepository balanceRepository) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.balanceRepository = balanceRepository;
    }

    /**
     * Получить балансы текущего пользователя.
     *
     * @return Список балансов пользователя.
     */
    @GetMapping("/user")
    public ResponseEntity<List<Balance>> getUserBalances() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        List<Balance> balances = balanceRepository.findByUser(currentUser);

        return ResponseEntity.ok(balances);
    }

    /**
     * Пополнить баланс текущего пользователя.
     *
     * @param payload Данные для пополнения баланса, включая валюту и сумму.
     * @return Статус операции с сообщением.
     */
    @PostMapping("/top-up")
    public ResponseEntity<?> topUpBalance(@RequestBody Map<String, Object> payload) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Пользователь не авторизован");
            }

            String username = userDetails.getUsername();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            String currencyCode = (String)  payload.get("currency");
            Double amount = Double.valueOf(payload.get("amount").toString());

            if (amount <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Сумма пополнения должна быть положительной"));
            }
            Long currencyId = Long.parseLong(currencyCode);

            // Проверяем существование валюты
            var currency = currencyRepository.findById(currencyId)
                    .orElseThrow(() -> new IllegalArgumentException("Валюта не найдена"));

            // Получаем текущий баланс пользователя по валюте
            var balance = balanceRepository.findByUserAndCurrency(currentUser, currency)
                    .orElseGet(() -> {
                        var newBalance = new Balance();
                        newBalance.setUser(currentUser);
                        newBalance.setCurrency(currency);
                        newBalance.setAmount(0.0);
                        return newBalance;
                    });

            // Обновляем баланс
            balance.setAmount(balance.getAmount() + amount);
            balanceRepository.save(balance);

            return ResponseEntity.ok(Map.of("message", "Баланс успешно пополнен!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Получить список всех валют.
     *
     * @return Список валют.
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(currencies);
    }
}
