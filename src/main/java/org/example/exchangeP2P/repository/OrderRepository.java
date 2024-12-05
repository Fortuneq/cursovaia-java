package org.example.exchangeP2P.repository;

import org.example.exchangeP2P.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.sourceCurrency) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.targetCurrency) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.status) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Order> search(String keyword);
}
