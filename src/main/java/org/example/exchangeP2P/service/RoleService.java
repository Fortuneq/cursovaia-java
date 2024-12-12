package org.example.exchangeP2P.service;

import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> listAll() {
        return roleRepository.findAll();
    }

    public Role get(Long id) {
        return roleRepository.findById(id).get();
    }
}