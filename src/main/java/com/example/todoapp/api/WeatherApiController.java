package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherApiController {
    private final WeatherClient weatherClient;

    public WeatherApiController(WeatherClient weatherClient) { this.weatherClient = weatherClient; }

    @GetMapping("/api/weather")
    public ResponseEntity<Map<String, Object>> weather(
            @RequestParam(defaultValue = "35.681236") double latitude,
            @RequestParam(defaultValue = "139.767125") double longitude,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate start = from == null ? today : from;
        LocalDate end = to == null ? start.plusDays(6) : to;
        // Forecasts are only available for a limited future range. Keep the panel useful
        // when a user opens an older or distant calendar month.
        if (end.isBefore(start) || start.isBefore(today) || end.isAfter(today.plusDays(15))) {
            start = today;
            end = today.plusDays(6);
        }
        WeatherClient.FetchResult result = weatherClient.fetch(latitude, longitude, start.toString(), end.toString());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("latitude", latitude);
        body.put("longitude", longitude);
        body.put("forecasts", result.getForecasts());
        if (result.isUnavailable()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Weather-Unavailable", "true");
            return ResponseEntity.ok().headers(headers).body(body);
        }
        return ResponseEntity.ok(body);
    }
}
