package org.example.exchangeP2P.repository;

import org.example.exchangeP2P.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    /**
     * Ищет пользователя по его уникальному имени пользователя (логину).
     *
     * @param username логин пользователя.
     * @return объект {@link User}, если пользователь с указанным логином существует, или null в противном случае.
     */
    Optional<User> findByUsername(String username);

    /**
     * Ищет пользователей, у которых имя, логин, телефон или электронная почта содержат указанный текст (без учета регистра).
     *
     * @param name часть имени пользователя.
     * @param username часть логина пользователя.
     * @param phone часть номера телефона пользователя.
     * @param email часть адреса электронной почты пользователя.
     * @return список пользователей, соответствующих критериям поиска.
     */
//    List<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCase(
//            String name, String username, String phone, String email);
//
//    /**
//     * Ищет пользователей, у которых есть роли с именем, содержащим указанный текст (без учета регистра).
//     *
//     * @param roleName часть имени роли.
//     * @return список пользователей, у которых есть роли, соответствующие критерию поиска.
//     */
//    List<User> findByRoles_NameContainingIgnoreCase(String roleName);
}