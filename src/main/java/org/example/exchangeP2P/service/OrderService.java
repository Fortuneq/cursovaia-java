package org.example.exchangeP2P.service;

import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public List<Order> listAll(String keyword) {
        return orderRepository.findAll();
    }
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    public Optional<Order> getOrderById(Long id) {
       return orderRepository.findById(id);
    }
}
