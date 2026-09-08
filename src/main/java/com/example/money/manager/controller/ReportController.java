package com.example.money.manager.controller;

import com.example.money.manager.entity.ExpenseEntity;
import com.example.money.manager.entity.ProfileEntity;
import com.example.money.manager.repository.ExpenseRepository;
import com.example.money.manager.repository.ProfileRepository;
import com.example.money.manager.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PdfExportService pdfExportService;
    private final ExpenseRepository expenseRepository;
    private final ProfileRepository profileRepository;

    @GetMapping("/download-pdf")
    public ResponseEntity<InputStreamResource> downloadExpenseReport(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String userEmail = principal.getName();
        ProfileEntity profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        // Default: Last 3 months
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusMonths(3);
        }

        // Validation 1: Start date cannot be after end date
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date.");
        }

        // Validation 2: Max 3 Months check (93 days max)
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days > 93) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date range exceeds maximum limit of 3 months.");
        }

        // Fetch data
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        // Convert to rows for PDF
        List<String[]> rows = new ArrayList<>();
        double totalAmount = 0.0;

        for (ExpenseEntity exp : expenses) {
            double amt = exp.getAmount() != null ? exp.getAmount().doubleValue() : 0.0;
            totalAmount += amt;

            rows.add(new String[]{
                    exp.getDate() != null ? exp.getDate().toString() : "-",
                    exp.getCategory() != null ? exp.getCategory().toString() : "General",
                    exp.getName() != null ? exp.getName() : "-",
                    String.format("%.2f", amt)
            });
        }

        // Generate PDF stream
        ByteArrayInputStream pdfStream = pdfExportService.generateExpensePdf(
                userEmail, startDate, endDate, rows, totalAmount
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SmartVault_Report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}