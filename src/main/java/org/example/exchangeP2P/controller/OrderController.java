package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Balance;
import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.BalanceRepository;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.example.exchangeP2P.service.BalanceService;
import org.example.exchangeP2P.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    private final BalanceService balanceService;

    private final BalanceRepository balanceRepository;
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderRepository orderRepository, CurrencyRepository currencyRepository, UserRepository userRepository, BalanceService balanceService, BalanceRepository balanceRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.balanceService = balanceService;
        this.balanceRepository = balanceRepository;
        this.orderService = orderService;
    }

    // Получить заявки текущего пользователя
    @GetMapping("/user")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User currentUser,
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


        orders = orderRepository.findByBuyerOrSeller(user,user);

        return ResponseEntity.ok(orders);
    }


    @GetMapping//массив заявок
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam(value = "keyword", required = false) String keyword,
                                                    @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                    Model model)  {
        List<Order> orders;

        Sort sortOrder;

        if ("desc".equals(sort)) {
            sortOrder = Sort.by(Sort.Order.desc("price"));
        } else {
            sortOrder = Sort.by(Sort.Order.asc("price"));
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));



        if (keyword != null && !keyword.isEmpty()) {
            orders = orderRepository.findByKeyword(keyword, sortOrder);
        } else  {
            orders = orderRepository.findAll(sortOrder);
        }


        // Передаем данные в модель
        model.addAttribute("listOrder", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return ResponseEntity.ok(orders);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Order orderRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));


        // Получаем исходную валюту
        Currency sourceCurrency = currencyRepository.findById(orderRequest.getSourceCurrency().getId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная исходная валюта"));

        // Получаем целевую валюту
        Currency targetCurrency = currencyRepository.findById(orderRequest.getTargetCurrency().getId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная целевая валюта"));

        Balance userBalance = balanceService.GetBalance(user, sourceCurrency);
        if (userBalance.getAmount() < orderRequest.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Недостаточно средств",
                    "availableBalance", userBalance.getAmount(),
                    "requiredAmount", orderRequest.getAmount()
            ));
        }

        if (sourceCurrency == targetCurrency){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Нельзя обменть валюту на ту же"
            ));
        }

        // Создаем новую заявку
        Order order = new Order();
        order.setSeller(user);
        order.setSourceCurrency(sourceCurrency);
        order.setTargetCurrency(targetCurrency);
        order.setAmount(orderRequest.getAmount());
        order.setPrice(orderRequest.getPrice());

        // Сохраняем заявку в базу данных
        orderRepository.save(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(currencies);
    }

    @PostMapping("/exchange/{orderId}")
    public ResponseEntity<?> exchangeOrder(@PathVariable Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Пользователь не авторизован");
        }

        String username = userDetails.getUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найден"));

        // Проверка на статус и владельца заявки
        if (!"ACTIVE".equals(order.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Заявка не активна"));
        }

        if (order.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Невозможно обменять свою заявку"));
        }


        // Проверка наличия средств на балансе
        Balance userBalance = balanceService.GetBalance(currentUser, order.getSourceCurrency());
        if (userBalance.getAmount() < order.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Недостаточный баланс"));
        }

        // Получение баланса второго пользователя, с которым происходит обмен
        User ownerUser = order.getSeller();
        Balance ownerBalance = balanceService.GetBalance(ownerUser, order.getTargetCurrency());

        if (ownerBalance.getAmount() < order.getAmount() * order.getPrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","У владельца заявки недостаточно средств для обмена"));
        }

        // Перерасчёт валют
        double exchangedAmount = order.getAmount() * order.getPrice(); // Переводим с учётом курса
        userBalance.setAmount(userBalance.getAmount() - order.getAmount());
        ownerBalance.setAmount(ownerBalance.getAmount() - exchangedAmount);

        // Обновляем балансы обоих пользователей
        balanceRepository.save(userBalance);
        balanceRepository.save(ownerBalance);

        // Переводим валюту на баланс владельца заявки
        Balance targetUserBalance = balanceService.GetBalance(currentUser, order.getTargetCurrency());
        targetUserBalance.setAmount(targetUserBalance.getAmount() + exchangedAmount);
        balanceRepository.save(targetUserBalance);

        // Переводим исходную валюту на баланс пользователя, принимающего заявку
        Balance sourceOwnerBalance = balanceService.GetBalance(ownerUser, order.getSourceCurrency());
        sourceOwnerBalance.setAmount(sourceOwnerBalance.getAmount() + order.getAmount());
        balanceRepository.save(sourceOwnerBalance);



        order.setStatus("DONE");
        order.setBuyer(currentUser);
        orderRepository.save(order);

        return ResponseEntity.ok("Заявка успешно обменяна");
    }
}
