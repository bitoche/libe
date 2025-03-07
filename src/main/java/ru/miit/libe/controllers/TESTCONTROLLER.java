package ru.miit.libe.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//deletewhen front added
@Controller
public class TESTCONTROLLER {
    @GetMapping("/")
    public String returnToSwagger(){
        return "redirect:/swagger-ui/index.html";
    }
}
