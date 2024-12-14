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

    /**
     * Метод для отображения домашней страницы.
     *
     * @return Имя шаблона для домашней страницы.
     */
    @GetMapping("/")
    public String serveHomePage(){
        return "index";
    }

    /**
     * Метод для отображения страницы с пользователями, доступен только для администратора.
     *
     * @return Имя шаблона страницы пользователей.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String serveUsersPage() {
        return "users";
    }

    /**
     * Метод для отображения формы входа. Если присутствует ошибка, показывается сообщение об ошибке.
     *
     * @param error Сообщение об ошибке входа, если оно есть.
     * @param model Модель для передачи атрибутов в шаблон.
     * @return Имя шаблона для страницы входа.
     */
    @RequestMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неверные учетные данные. Пожалуйста, попробуйте снова.");
        }
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    /**
     * Метод для отображения формы регистрации нового пользователя.
     *
     * @param model Модель для передачи атрибутов в шаблон.
     * @return Имя шаблона для страницы регистрации.
     */
    @RequestMapping("/register")
    public String showRegisterForm(Model model) {
        User user = new User();
        model.addAttribute("user", user); // Передает новый объект User
        return "register"; // Возвращает register.html
    }

    /**
     * Метод для обработки регистрации пользователя. Если форма содержит ошибки, они выводятся на страницу.
     *
     * @param user Объект пользователя, который заполняет форму.
     * @param bindingResult Результат проверки формы, который содержит ошибки, если они есть.
     * @param model Модель для передачи атрибутов в шаблон.
     * @return Перенаправление на страницу входа или повторная загрузка страницы регистрации с ошибками.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "register";
        }

        try {
            userService.registerUser(user, "USER");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка регистрации: " + e.getMessage());
            return "register";
        }
        return "redirect:/login";
    }

    /**
     * Метод для отображения страницы с информацией о авторе.
     *
     * @return Имя шаблона для страницы с информацией о авторе.
     */
    @GetMapping("/author")
    public String serveAboutPage() {
        return "author";
    }

    /**
     * Метод для отображения страницы профиля пользователя.
     *
     * @return Имя шаблона для страницы профиля.
     */
    @GetMapping("/profile")
    public String serveProfilePage() {
        return "profile";
    }

    /**
     * Метод для отображения страницы создания заявки на обмен.
     *
     * @return Имя шаблона для страницы создания заявки.
     */
    @GetMapping("/orders/create")
    public String serveOrdersPage() {
        return "create_order";
    }

    /**
     * Метод для отображения страницы с заказами текущего пользователя.
     *
     * @return Имя шаблона для страницы с заказами пользователя.
     */
    @GetMapping("/orders/by_user")
    public String serveUserOrdersPage() {
        return "user_orders";
    }

    /**
     * Метод для отображения страницы добавления баланса.
     *
     * @return Имя шаблона для страницы добавления баланса.
     */
    @GetMapping("/balances/add")
    public String serveAddBalancePage() {
        return "add_balance";
    }

    /**
     * Метод для отображения страницы редактирования заказов администратором.
     *
     * @return Имя шаблона для страницы редактирования заказов.
     */
    @GetMapping("/admin/edit_orders")
    @PreAuthorize("hasRole('ADMIN')")
    public String serveEditOrders() {
        return "admin_edit_orders";
    }

    /**
     * Метод для отображения панели администратора.
     *
     * @return Имя шаблона для панели администратора.
     */
    @GetMapping("/admin/panel")
    public String serveAdminPanel() {
        return "admin_panel";
    }

    /**
     * Метод для отображения панели управления пользователями администратором.
     *
     * @return Имя шаблона для панели управления пользователями.
     */
    @GetMapping("/admin/list_users")
    public String serveAdminUsersPanel() {
        return "admin_users";
    }

    /**
     * Метод для отображения панели заказов администратору.
     *
     * @return Имя шаблона для панели заказов.
     */
    @GetMapping("/admin/list_orders")
    public String serveAdminListOrdersPanel() {
        return "admin_orders";
    }

    /**
     * Метод для отображения панели валют администратору.
     *
     * @return Имя шаблона для панели валют.
     */
    @GetMapping("/admin/list_currencies")
    public String serveAdminListCurrenciesPanel() {
        return "admin_currency";
    }

}