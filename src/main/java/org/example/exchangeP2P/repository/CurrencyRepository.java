package org.example.exchangeP2P.repository;

import org.example.exchangeP2P.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    // Дополнительные методы для работы с валютами, если нужно
}
