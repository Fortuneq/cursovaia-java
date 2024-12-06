package org.example.exchangeP2P.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code",nullable = false, unique = true)
    private String code; // Например, "USD", "EUR", "RUB"

    @Column(name = "name",nullable = false)
    private String name; // Полное название валюты, например, "Доллар США"

    @Column(name = "symbol")
    private String symbol; // Например, "$", "€", "₽"
}