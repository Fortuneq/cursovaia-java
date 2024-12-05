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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Владелец ордера

    @Column(nullable = false)
    private String currencyFrom; // Валюта, которую пользователь хочет обменять

    @Column(nullable = false)
    private String currencyTo; // Валюта, которую пользователь хочет получить

    @Column(nullable = false)
    private Double amount; // Сумма обмена

    @Column(nullable = false)
    private Double rate; // Курс обмена

    @Column(nullable = false)
    private Boolean isActive = true; // Статус ордера
}
