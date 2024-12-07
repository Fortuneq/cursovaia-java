package org.example.exchangeP2P.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_balances")
public class UserBalances {

    public UserBalances() {
        this.balance = 0; // Устанавливаем значение по умолчанию
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false,columnDefinition = "double default 0")
    private double balance;

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void subtractBalance(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new IllegalArgumentException("Недостаточно средств");
        }
    }
}
