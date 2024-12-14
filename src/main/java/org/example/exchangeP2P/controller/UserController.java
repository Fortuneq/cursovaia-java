package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.service.RoleService;
import org.example.exchangeP2P.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
public class UserController {

    /** Сервис для управления пользователями. */
    @Autowired
    private UserService userService;

    /** Сервис для управления ролями. */
    @Autowired
    private RoleService roleService;

    /**
     * Получить текущего авторизованного пользователя.
     *
     * @param principal Содержит информацию о текущем пользователе.
     * @return ResponseEntity с объектом User, если пользователь найден, или статус UNAUTHORIZED, если не авторизован.
     */
    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Получить данные пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return ResponseEntity с объектом User, если пользователь найден, или статус NOT_FOUND, если пользователь не найден.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.get(id);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Обновить данные пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, чьи данные нужно обновить.
     * @param user Обновленный объект пользователя.
     * @param principal Информация о текущем авторизованном пользователе.
     * @return ResponseEntity с обновленным объектом User, или статус NOT_FOUND, если пользователь не найден.
     * Если текущий пользователь не имеет прав администратора или не является самим собой, то вернется статус FORBIDDEN.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user, Principal principal) {
        User existingUser = userService.get(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User currentUser = userService.findByUsername(principal.getName());

        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))
                && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());

        Set<Role> roles = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                Role existingRole = roleService.get(role.getId());
                if (existingRole != null) {
                    roles.add(existingRole);
                }
            });
        }
        existingUser.setRoles(roles);

        User updatedUser = userService.save(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удалить пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, которого необходимо удалить.
     * @return ResponseEntity без содержимого (no content), если операция выполнена успешно.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получить список всех ролей системы.
     *
     * @return ResponseEntity со списком всех доступных ролей, если они найдены.
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = roleService.listAll();
        return ResponseEntity.ok(roles);
    }
}
