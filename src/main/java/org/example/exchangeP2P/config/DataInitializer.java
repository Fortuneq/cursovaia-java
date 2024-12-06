package org.example.exchangeP2P.config;

import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.CurrencyRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final CurrencyRepository currencyRepository;


    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CurrencyRepository currencyRepository){
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currencyRepository = currencyRepository;
    }
    @Override
    public void run(String... args) throws Exception{
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
        if(userRepository.findByUsername("Vlad").isEmpty()){
            User admin = new User();
            admin.setUsername("Vlad");
            admin.setPassword(passwordEncoder.encode("11"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }
        addCurrency("USD", "Доллар США", "$");
        addCurrency("EUR", "Евро", "€");
        addCurrency("RUB", "Российский рубль", "₽");
    }


    private void addCurrency(String code, String name, String symbol) {
        currencyRepository.findByCode(code).orElseGet(() -> {
            Currency currency = new Currency();
            currency.setCode(code);
            currency.setName(name);
            currency.setSymbol(symbol);
            return currencyRepository.save(currency);
        });
    }
}