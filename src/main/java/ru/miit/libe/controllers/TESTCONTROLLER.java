package ru.miit.libe.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class TESTCONTROLLER {
    @GetMapping("/")
    public String returnToSwagger() {
        return "forward:/index.html";
    }

    @GetMapping("/test/login_page")
    public String getLoginPage(){
        return "forward:/login_page.html";
    }
}