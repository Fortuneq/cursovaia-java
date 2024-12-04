package org.example.exchangeP2P.repository;

import org.example.exchangeP2P.entity.ExchangeRequest;
import org.example.exchangeP2P.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findByUserAndStatus(User user, ExchangeRequest.Status status);
}
