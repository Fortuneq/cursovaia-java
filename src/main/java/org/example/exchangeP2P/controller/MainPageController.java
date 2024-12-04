package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.ExchangeRequest;
import org.example.exchangeP2P.service.CurrencyService;
import org.example.exchangeP2P.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final CurrencyService currencyService; // Сервис для получения валют из базы данных

    @Autowired
    public ExchangeController(ExchangeService exchangeService, CurrencyService currencyService) {
        this.exchangeService = exchangeService;
        this.currencyService = currencyService;
    }

    @GetMapping
    public String showOrders(@RequestParam(required = false) String currency,
                             @RequestParam(required = false) Double amount,
                             @RequestParam(required = false) Double price, Model model) {
        List<ExchangeRequest> orders = exchangeService.getFilteredOrders(currency, amount, price);
        List<String> currencies = currencyService.getAllCurrencies(); // Получаем валюты из базы данных

        model.addAttribute("orders", orders);
        model.addAttribute("currencies", currencies);

        return "index";
    }
}