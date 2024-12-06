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

    @Column(name = "currency_from", nullable = false)
    private String sourceCurrency;

    @Column(name = "currency_to", nullable = false)
    private String targetCurrency;

    @Column(name = "price", nullable = false)
    private double price;


    @Column(name = "amount", nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String status = "ACTIVE";
}
