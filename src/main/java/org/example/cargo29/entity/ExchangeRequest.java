package org.example.cargo29.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "exchange_requests")
public class ExchangeRequest {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @ManyToOne
    private User user;

    @Setter
    @Getter
    private String sourceCurrency;
    @Setter
    @Getter
    private Double sourceAmount;

    @Setter
    @Getter
    private String targetCurrency;
    @Setter
    @Getter
    private Double targetAmount;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, COMPLETED, FAILED
    }

    // Getters and setters
}