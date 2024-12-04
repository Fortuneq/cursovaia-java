package org.example.exchangeP2P.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Currency {

    @Id
    private String name;  // Валюта будет идентифицироваться по названию

    // Конструкторы, геттеры и сеттеры

    public Currency() {}

    public Currency(String name) {
        this.name = name;
    }

}
