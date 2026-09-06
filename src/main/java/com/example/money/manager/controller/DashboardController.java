package com.example.money.manager.controller;

import com.example.money.manager.repository.ExpenseRepository;
import com.example.money.manager.service.DashboardService;
import com.example.money.manager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor

@RequestMapping("dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashBoardData(){
        Map<String,Object> dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);

    }
}
