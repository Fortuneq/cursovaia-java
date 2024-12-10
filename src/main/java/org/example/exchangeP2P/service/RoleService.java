package org.example.exchangeP2P.service;

import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    /**
     * Репозиторий для работы с базой данных ролей.
     */
    private final RoleRepository roleRepository;

    /**
     * Конструктор для внедрения зависимости репозитория ролей.
     *
     * @param roleRepository репозиторий ролей.
     */
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Возвращает список всех ролей из базы данных.
     *
     * @return список объектов {@link Role}.
     */
    public List<Role> listAll() {
        return roleRepository.findAll();
    }

    /**
     * Возвращает роль по её идентификатору.
     * Если роль не найдена, выбрасывается исключение {@link java.util.NoSuchElementException}.
     *
     * @param id идентификатор роли.
     * @return объект {@link Role}, соответствующий указанному идентификатору.
     */
    public Role get(Long id) {
        return roleRepository.findById(id).get();
    }
}