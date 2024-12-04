package org.example.cargo29.service;

import org.example.cargo29.entity.ExchangeRequest;
import org.example.cargo29.entity.User;
import org.example.cargo29.repository.ExchangeRequestRepository;
import org.example.cargo29.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void createRequest(ExchangeRequest order) {
    }
}
