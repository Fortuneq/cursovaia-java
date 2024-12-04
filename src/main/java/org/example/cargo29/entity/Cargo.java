package org.example.cargo29.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Cargo {
    private Long id; // ID
    private String name; // Название груза
    private String content; // Содержимое груза
    private String cityofdeparture; // Город отправки груза
    private LocalDate dateofdeparture; // Дата отправки груза
    private String cityofarrival; // Город прибытия груза
    private LocalDate dateofarrival; // Дата прибытия груза

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCityofdeparture() {
        return cityofdeparture;
    }

    public void setCityofdeparture(String cityofdeparture) {
        this.cityofdeparture = cityofdeparture;
    }

    public LocalDate getDateofdeparture() {
        return dateofdeparture;
    }

    public void setDateofdeparture(LocalDate dateofdeparture) {
        this.dateofdeparture = dateofdeparture;
    }

    public String getCityofarrival() {
        return cityofarrival;
    }

    public void setCityofarrival(String cityofarrival) {
        this.cityofarrival = cityofarrival;
    }

    public LocalDate getDateofarrival() {
        return dateofarrival;
    }

    public void setDateofarrival(LocalDate dateofarrival) {
        this.dateofarrival = dateofarrival;
    }

    @Override
    public String toString() {
        return "Cargo [id=" + id + ", name=" + name + ", content=" + content +
                ", cityofdeparture=" + cityofdeparture + ", dateofdeparture=" + dateofdeparture +
                ", cityofarrival=" + cityofarrival + ", dateofarrival=" + dateofarrival + "]";
    }
}
