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
    @JoinColumn(nullable = false)
    private Currency sourceCurrency;

    @ManyToOne
    @JoinColumn( nullable = false)
    private Currency targetCurrency;

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
