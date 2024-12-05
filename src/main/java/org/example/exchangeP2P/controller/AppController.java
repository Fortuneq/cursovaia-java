package org.example.exchangeP2P.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.exchangeP2P.entity.Order;
import org.example.exchangeP2P.repository.OrderRepository;
import org.example.exchangeP2P.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AppController {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderRepository repo;

    @RequestMapping("/")
    public String viewHomePage(Model model, @Param("keyword") String keyword, @Param("sort") String sort) {
        List<Order> listOrder;
        if (keyword != null) {
            listOrder = service.listAll(keyword);
        } else {
            listOrder = service.listAll(null);
        }
        model.addAttribute("listOrder", listOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "index";
    }
}