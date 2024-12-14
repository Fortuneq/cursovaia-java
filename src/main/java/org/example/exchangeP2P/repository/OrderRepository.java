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

    List<Order> findByBuyerOrSeller(User buyer,User seller );

    @Query("SELECT o FROM Order o WHERE (o.sourceCurrency.name LIKE %:keyword% OR o.sourceCurrency.code LIKE %:keyword% OR o.targetCurrency.name LIKE %:keyword% OR o.targetCurrency.code LIKE %:keyword%)")
    List<Order> findByKeyword( @Param("keyword") String keyword, Sort sort);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.sourceCurrency.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.targetCurrency.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.status) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Order> searchOrders(@Param("keyword") String keyword, Sort sort);

}

