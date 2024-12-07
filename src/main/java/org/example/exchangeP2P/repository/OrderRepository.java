package org.example.exchangeP2P.repository;

import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.sourceCurrency.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.targetCurrency.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.status) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Order> search(String keyword);


    List<Order> findByUserId(Long userId);

    // Найти ордера по пользователю
    List<Order> findByUser(User user, Sort sort);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND (o.sourceCurrency.name LIKE %:keyword% OR o.sourceCurrency.code LIKE %:keyword% OR o.targetCurrency.name LIKE %:keyword% OR o.targetCurrency.code LIKE %:keyword%)")
    List<Order> findByUserAndKeyword(@Param("user") User user, @Param("keyword") String keyword, Sort sort);
}

