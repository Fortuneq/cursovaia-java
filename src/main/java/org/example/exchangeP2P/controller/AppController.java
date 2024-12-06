package org.example.exchangeP2P.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AppController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private OrderRepository repo;

    @RequestMapping("/orders")
    public String viewHomePage(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "sort", defaultValue = "asc") String sort,
                               Model model) {
        Sort sortOrder;

        // Применяем сортировку по полю 'price' в зависимости от направления
        if ("desc".equals(sort)) {
            sortOrder = Sort.by(Sort.Order.desc("price"));
        } else {
            sortOrder = Sort.by(Sort.Order.asc("price"));
        }

        // Применяем фильтрацию по ключевому слову, если оно указано
        List<Order> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = orderRepository.search(keyword); // Здесь вы ищете по ключевому слову
        } else {
            orders = orderRepository.findAll(sortOrder); // Сортируем по 'price'
        }

        model.addAttribute("listOrder", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "index";
    }

}