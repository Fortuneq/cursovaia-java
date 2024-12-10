package org.example.exchangeP2P.service;


import org.example.exchangeP2P.entity.Currency;
import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.Role;
import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.repository.RoleRepository;
import org.example.exchangeP2P.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    /** Репозиторий для управления пользователями. */
    private final UserRepository userRepository;

    /** Репозиторий для управления ролями. */
    private final RoleRepository roleRepository;

    /** Кодировщик паролей для защиты учетных данных. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userRepository репозиторий пользователей.
     * @param roleRepository репозиторий ролей.
     * @param passwordEncoder кодировщик паролей.
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Возвращает список пользователей, соответствующих ключевому слову.
     * Ищет пользователей по имени, логину, телефону, электронной почте или названию роли.
     * Если ключевое слово отсутствует, возвращает всех пользователей.
     *
     * @param keyword ключевое слово для поиска.
     * @return список пользователей, соответствующих критериям поиска.
     */
//    public List<User> listAll(String keyword) {
//        if (keyword != null) {
////            List<User> usersByName = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCase(
////                    keyword, keyword, keyword, keyword);
////            List<User> usersByRole = userRepository.findByRoles_NameContainingIgnoreCase(keyword);
//
//            Set<User> combinedUsers = new HashSet<>(usersByName);
//            combinedUsers.addAll(usersByRole);
//
//            return new ArrayList<>(combinedUsers);
//        }
//        return userRepository.findAll();
//    }

    /**
     * Регистрирует нового пользователя с указанной ролью.
     * Кодирует пароль пользователя, устанавливает роль и сохраняет пользователя в базе данных.
     *
     * @param user объект пользователя.
     * @param roleName имя роли.
     * @return зарегистрированный пользователь.
     */
    public User registerUser(User user, String roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role selectedRole = roleRepository.findByName(roleName).orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(selectedRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Ищет пользователя по имени пользователя (логину).
     *
     * @param username логин пользователя.
     * @return найденный пользователь или null, если пользователь отсутствует.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    /**
     * Возвращает пользователя по его идентификатору.
     * Если пользователь отсутствует, выбрасывается исключение {@link RuntimeException}.
     *
     * @param id идентификатор пользователя.
     * @return объект пользователя.
     */
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден."));
    }

    /**
     * Сохраняет пользователя, обновляя его данные.
     * Пароль кодируется перед сохранением.
     *
     * @param user объект пользователя.
     * @return сохраненный пользователь.
     */
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
