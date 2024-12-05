package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.RoleRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }
    @PostMapping("/users/{id}/toggleRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleRole(@PathVariable("id") Long id, @RequestParam("roleName") String roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + roleName));
        Set<Role> roles = new HashSet<>();
        if ("ADMIN".equals(roleName)) {
            roles.add(role);
        } else {
            roles.add(roleRepository.findByName("USER").orElseThrow(() -> new IllegalArgumentException("Role USER not found")));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "redirect:/admin/users";
    }
}