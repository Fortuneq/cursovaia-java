package org.example.cargo29.repository;

import org.example.cargo29.entity.ExchangeRequest;
import org.example.cargo29.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findByUserAndStatus(User user, ExchangeRequest.Status status);
}
