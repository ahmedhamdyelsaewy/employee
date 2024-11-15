package com.digivisions.task.employeemanager.validation;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailValidationService {

    private final RestTemplate restTemplate;

    public EmailValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isValidEmail(String email) {
        // Replace with your actual email validation API URL
        String apiUrl = "https://api.emailvalidation.com/validate?email=" + email;
        EmailValidationResponse response = restTemplate.getForObject(apiUrl, EmailValidationResponse.class);
        return response != null && response.isValid();
    }
}