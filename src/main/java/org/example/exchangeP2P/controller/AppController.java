package org.example.exchangeP2P.controller;

import org.example.exchangeP2P.entity.User;
import org.example.exchangeP2P.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
public class AppController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String serveHomePage(){
        return "index";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String serveUsersPage() {
        return "users";
    }

    @RequestMapping("/login")
    public String showLoginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "login";
        }

        try {
            userService.registerUser(user, "USER");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка регистрации: " + e.getMessage());
            return "login";
        }
        return "redirect:/login";
    }

    @GetMapping("/author")
    public String serveAboutPage() {
        return "author";
    }

    @GetMapping("/profile")
    public String serveProfilePage() {
        return "profile";
    }


    @GetMapping("/orders/create")
    public String serveOrdersPage() {
        return "create_order";
    }

    @GetMapping("/orders/by_user")
    public String serveUserOrdersPage() {
        return "user_orders";
    }

    @GetMapping("/balances/add")
    public String serveAddBalancePage() {
        return "add_balance";
    }

    @GetMapping("/admin/edit_orders")
    @PreAuthorize("hasRole('ADMIN')")
    public String serveEditOrders() {
        return "admin_edit_orders";
    }

    @GetMapping("/admin/panel")
    public String serveAdminPanel() {
        return "admin_panel";
    }

    @GetMapping("/admin/list_users")
    public String serveAdminUsersPanel() {
        return "admin_users";
    }

    @GetMapping("/admin/list_orders")
    public String serveAdminListOrdersPanel() {
        return "admin_orders";
    }

    @GetMapping("/admin/list_currencies")
    public String serveAdminListCurrenciesPanel() {
        return "admin_currency";
    }

}