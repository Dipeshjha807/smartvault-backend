package com.example.money.manager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class Emailservice {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name:SmartVault}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        String url = "https://api.brevo.com/v3/smtp/email";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            Map<String, Object> sender = Map.of("name", senderName, "email", senderEmail);
            Map<String, Object> to = Map.of("email", toEmail);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", sender);
            requestBody.put("to", Collections.singletonList(to));
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("===> [BREVO] Sending mail via HTTPS API to: " + toEmail);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("===> [BREVO SUCCESS] Response status: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("===> [BREVO ERROR] Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}