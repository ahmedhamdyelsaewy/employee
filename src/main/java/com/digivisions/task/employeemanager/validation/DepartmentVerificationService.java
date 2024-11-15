package com.digivisions.task.employeemanager.validation;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DepartmentVerificationService {

    private final RestTemplate restTemplate;

    public DepartmentVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isValidDepartment(String department) {
        // Call to the third-party department verification API
        String apiUrl = "https://api.departmentverification.com/validate?department=" + department;
        DepartmentVerificationResponse response = restTemplate.getForObject(apiUrl, DepartmentVerificationResponse.class);
        return response != null && response.isValid();
    }
}
