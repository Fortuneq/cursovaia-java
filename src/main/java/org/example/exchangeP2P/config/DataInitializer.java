package org.example.exchangeP2P.config;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.ExchangeRequest;
import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
import org.example.exchangeP2P.repository.ExchangeRequestRepository;
import org.example.exchangeP2P.repository.RoleRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CurrencyRepository currencyRepository,
                           ExchangeRequestRepository exchangeRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Создание ролей
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("USER");
            return roleRepository.save(role);
        });

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        // Создание администратора, если его нет
        if (!userRepository.findByUsername("DimaK").isPresent()) {
            User admin = new User();
            admin.setUsername("DimaK");
            admin.setPassword(passwordEncoder.encode("7301"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }

        // Создание валют, если их нет в базе данных
        createCurrency("USD");
        createCurrency("EUR");
        createCurrency("RUB");
        createCurrency("BTC");

        // Создание ордера, если его нет в базе
        if (exchangeRequestRepository.count() == 0) {
            ExchangeRequest order = new ExchangeRequest();
            order.setCurrency("USD");
            order.setAmount(100.0);
            order.setPrice(75.0);
            order.setUser(userRepository.findByUsername("DimaK").get());
            exchangeRequestRepository.save(order);
        }
    }

    private void createCurrency(String name) {
        if (!currencyRepository.existsById(name)) {
            Currency currency = new Currency(name);
            currencyRepository.save(currency);
        }
    }
}
