package com.example.money.manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/check")
    public  String heathcheck(){
        return "heathcheck";
    }

}
