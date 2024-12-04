package org.example.exchangeP2P.service;

import org.example.exchangeP2P.entity.ExchangeRequest;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.ExchangeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeService {
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserService userService;

    @Autowired
    public ExchangeService(ExchangeRequestRepository exchangeRequestRepository, UserService userService) {
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.userService = userService;
    }

    // Создание заявки на обмен валюты
    public void createRequest(User user, String sourceCurrency, Double sourceAmount,
                              String targetCurrency, Double targetAmount) {
        ExchangeRequest request = new ExchangeRequest();
        request.setUser(user);
        request.setSourceCurrency(sourceCurrency);
        request.setSourceAmount(sourceAmount);
        request.setTargetCurrency(targetCurrency);
        request.setTargetAmount(targetAmount);
        request.setStatus(ExchangeRequest.Status.PENDING); // Заявка в ожидании
        exchangeRequestRepository.save(request);
    }

    // Принятие заявки на обмен
    public void acceptRequest(User user, Long requestId) {

    }


    public List<ExchangeRequest> getAllOrders() {
        return exchangeRequestRepository.findAll();
    }


    public List<ExchangeRequest> getFilteredOrders(String currency, Double minAmount, Double minPrice, Double maxPrice, String username) {
        if (currency != null && minAmount != null) {
            return exchangeRequestRepository.findByCurrencyAndAmountGreaterThanEqual(currency, minAmount);
        } else if (currency != null && minPrice != null && maxPrice != null) {
            return exchangeRequestRepository.findByCurrencyAndPriceBetween(currency, minPrice, maxPrice);
        } else if (currency != null && username != null) {
            return exchangeRequestRepository.findByCurrencyAndUserUsername(currency, username);
        }
        return exchangeRequestRepository.findAll(); // Возвращаем все ордера, если фильтры не указаны
    }

    public void createRequest(ExchangeRequest order) {
    }
}
