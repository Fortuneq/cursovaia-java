package org.example.exchangeP2P.service;
import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<String> getAllCurrencies() {
        // Получаем список всех валют из базы данных
        return currencyRepository.findAll().stream()
                .map(Currency::getName) // Извлекаем только имена валют
                .toList();
    }
}
