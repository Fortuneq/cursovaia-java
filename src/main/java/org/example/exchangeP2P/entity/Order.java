package org.example.exchangeP2P.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceCurrency; // Исходная валюта

    @Column(name = "currency_from", nullable = false)
    private double amount; // Сумма в исходной валюте

    @Column(name = "currency_to", nullable = false)
    private String targetCurrency; // Целевая валюта

    @Column(name = "amount", nullable = false)
    private double price; // Цена в целевой валюте

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String status = "ACTIVE"; // Статус ордера (ACTIVE, COMPLETED, CANCELLED)
}
