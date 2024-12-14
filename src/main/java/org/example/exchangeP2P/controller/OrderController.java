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

/**
 * Контроллер для работы с заявками на обмен валют.
 * Обрабатывает запросы для создания заявок, получения активных заявок и их обмена.
 */
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

    /**
     * Получить все заявки текущего пользователя.
     *
     * @param currentUser текущий пользователь, авторизованный в системе
     * @param keyword строка для поиска заявок по ключевому слову (необязательный параметр)
     * @param sort сортировка заявок по цене (по умолчанию по возрастанию)
     * @return список заявок текущего пользователя
     */
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
        orders = orderRepository.findByBuyerOrSeller(user, user);

        return ResponseEntity.ok(orders);
    }

    /**
     * Получить все заявки на обмен валют, с возможностью фильтрации и сортировки.
     *
     * @param keyword строка для поиска по заявкам (необязательный параметр)
     * @param sort направление сортировки по цене (по умолчанию "asc")
     * @param model модель для отображения заявок на страницах
     * @return список всех заявок с применением фильтрации и сортировки
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam(value = "keyword", required = false) String keyword,
                                                    @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                    Model model) {
        List<Order> orders;
        Sort sortOrder = "desc".equals(sort) ? Sort.by(Sort.Order.desc("price")) : Sort.by(Sort.Order.asc("price"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (keyword != null && !keyword.isEmpty()) {
            orders = orderRepository.findByKeyword(keyword, sortOrder);
        } else {
            orders = orderRepository.findAll(sortOrder);
        }

        // Передаем данные в модель для отображения
        model.addAttribute("listOrder", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return ResponseEntity.ok(orders);
    }

    /**
     * Создать заявку на обмен валют.
     *
     * @param orderRequest данные заявки, полученные от пользователя
     * @return ответ с созданной заявкой или ошибкой
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Order orderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Получаем валюты
        Currency sourceCurrency = currencyRepository.findById(orderRequest.getSourceCurrency().getId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная исходная валюта"));
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

        if (sourceCurrency == targetCurrency) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Нельзя обменивать валюту на ту же"));
        }

        // Создаем заявку
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

    /**
     * Получить список всех доступных валют.
     *
     * @return список доступных валют
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(currencies);
    }

    /**
     * Обменять заявку на валюту.
     *
     * @param orderId идентификатор заявки, которую нужно обменять
     * @return результат обмена или сообщение об ошибке
     */
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
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        // Проверка на статус и владельца заявки
        if (!"ACTIVE".equals(order.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Заявка не активна"));
        }

        if (order.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Невозможно обменять свою заявку"));
        }

        // Проверка наличия средств
        Balance userBalance = balanceService.GetBalance(currentUser, order.getSourceCurrency());
        if (userBalance.getAmount() < order.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Недостаточно средств"));
        }

        // Проверка средств владельца заявки
        User ownerUser = order.getSeller();
        Balance ownerBalance = balanceService.GetBalance(ownerUser, order.getTargetCurrency());
        if (ownerBalance.getAmount() < order.getAmount() * order.getPrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "У владельца заявки недостаточно средств для обмена"));
        }

        // Перерасчёт и обновление баланса
        double exchangedAmount = order.getAmount() * order.getPrice();
        userBalance.setAmount(userBalance.getAmount() - order.getAmount());
        ownerBalance.setAmount(ownerBalance.getAmount() - exchangedAmount);
        balanceRepository.save(userBalance);
        balanceRepository.save(ownerBalance);

        Balance targetUserBalance = balanceService.GetBalance(currentUser, order.getTargetCurrency());
        targetUserBalance.setAmount(targetUserBalance.getAmount() + exchangedAmount);
        balanceRepository.save(targetUserBalance);

        Balance sourceOwnerBalance = balanceService.GetBalance(ownerUser, order.getSourceCurrency());
        sourceOwnerBalance.setAmount(sourceOwnerBalance.getAmount() + order.getAmount());
        balanceRepository.save(sourceOwnerBalance);

        order.setStatus("DONE");
        order.setBuyer(currentUser);
        orderRepository.save(order);

        return ResponseEntity.ok("Заявка успешно обменяна");
    }
}
