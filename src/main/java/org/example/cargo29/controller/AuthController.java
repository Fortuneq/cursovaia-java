package org.example.cargo29.controller;

import org.example.cargo29.entity.Role;
import org.example.cargo29.entity.User;
import org.example.cargo29.repository.RoleRepository;
import org.example.cargo29.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;


@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("user", new User());
        return "login"; // Используем один шаблон для регистрации и входа
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register")
    public @ResponseBody String registerUser(@Valid @ModelAttribute("user") User user,
                                             BindingResult result,
                                             Model model){
        if(result.hasErrors()){
            return "Ошибка регистрации: " + result.getAllErrors().toString();
        }

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return "Имя пользователя уже существует";
        }

        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Назначение роли "USER" по умолчанию
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        userRepository.save(user);

        return "Регистрация прошла успешно!";
    }
}