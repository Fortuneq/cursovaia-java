package org.example.exchangeP2P.entity;

import jakarta.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;


@Setter
@Getter
@Data
@Entity
@Table(name = "users")
public class User{
    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Имя пользователя обязательно")
    @Column(nullable=false, unique=true)
    private String username;
    @NotBlank(message = "Пароль обязателен")
    @Column(nullable=false)
    private String password;

    /**
     * Набор ролей, связанных с пользователем.
     * Реализована связь многие-ко-многим с использованием таблицы связей "user_roles".
     * Загрузка осуществляется с использованием режима {@link FetchType#EAGER}.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
