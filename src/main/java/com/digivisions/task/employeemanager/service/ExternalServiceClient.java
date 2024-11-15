package com.digivisions.task.employeemanager.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalServiceClient {

    private static final String WEATHER_API_URL = "https://api.weatherapi.com/v1/current.json?key=YOUR_API_KEY&q=London";

    public String getWeather() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(WEATHER_API_URL, String.class);
        return response;
    }
}
