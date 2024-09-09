package com.application.Application.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/home")
    public String controller(HttpServletRequest request){
        return "My Name is Jyoti Kumari"+request.getSession().getId();
    }
}
