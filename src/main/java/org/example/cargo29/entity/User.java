package org.example.cargo29.entity;

import jakarta.persistence.*;
import java.util.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
public class User {
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String username;
    @Setter
    @Getter
    private String password;

    @Setter
    @Getter
    @ElementCollection
    private Map<String, Double> balances = new HashMap<>(); // Ключ: валюта, Значение: сумма

    @Setter
    @Getter
    @OneToMany(mappedBy = "user")
    private List<ExchangeRequest> exchangeRequests;

}
