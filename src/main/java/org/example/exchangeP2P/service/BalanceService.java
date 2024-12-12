package org.example.exchangeP2P.service;

import org.example.exchangeP2P.entity.Balance;
import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.BalanceRepository;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository, CurrencyRepository currencyRepository) {
        this.balanceRepository = balanceRepository;
        this.currencyRepository = currencyRepository;
    }

    public void initializeBalances(User user) {
        List<Currency> currencies = currencyRepository.findAll();

        for (Currency currency : currencies) {
            balanceRepository.findByUserAndCurrency(user, currency)
                    .orElseGet(() -> {
                        Balance balance = new Balance();
                        balance.setUser(user);
                        balance.setCurrency(currency);
                        balance.setAmount(0.0);
                        return balanceRepository.save(balance);
                    });
        }
    }

     public Balance GetBalance(User user,Currency currency) {
       Optional<Balance> optBalance =  balanceRepository.findByUserAndCurrency(user,currency);

       return optBalance.get();
    }
}
