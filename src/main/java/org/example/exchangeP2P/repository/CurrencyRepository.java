package org.example.exchangeP2P.repository;

import org.example.exchangeP2P.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
   Optional<Currency> findByName(String name );

   Optional<Currency> findByCode(String code );
}