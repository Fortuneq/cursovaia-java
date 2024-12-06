package org.example.exchangeP2P.entity;

import jakarta.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;


@Setter
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {
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

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserBalances> balances = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="users_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<org.example.exchangeP2P.entity.Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    public void addFunds(Currency currency, double amount) {
        UserBalances userBalance = getBalanceForCurrency(currency);
        if (userBalance == null) {
            userBalance = new UserBalances();
            userBalance.setUser(this);
            userBalance.setCurrency(currency);
            userBalance.setBalance(amount);
            balances.add(userBalance);
        } else {
            userBalance.addBalance(amount);
        }
    }


    public UserBalances getBalanceForCurrency(Currency currency) {
        return balances.stream()
                .filter(userBalance -> userBalance.getCurrency().equals(currency))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
