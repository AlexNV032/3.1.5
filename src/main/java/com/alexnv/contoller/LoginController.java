package com.alexnv.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }
    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin";
    }
    @GetMapping("/user")
    public String showUserPage() {
        return "user";
    }
}
