package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolidayApiController {

    private final HolidayClient holidayClient;

    public HolidayApiController(HolidayClient holidayClient) {
        this.holidayClient = holidayClient;
    }

    @GetMapping("/api/holidays")
    public ResponseEntity<Map<String, String>> holidays(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        HolidayClient.FetchResult result = holidayClient.fetch();
        Map<String, String> holidays = result.holidays();
        Map<String, String> filteredHolidays = new LinkedHashMap<>();

        holidays.forEach((date, name) -> {
            LocalDate holidayDate = LocalDate.parse(date);
            boolean afterFrom = from == null || !holidayDate.isBefore(from);
            boolean beforeTo = to == null || !holidayDate.isAfter(to);
            if (afterFrom && beforeTo) {
                filteredHolidays.put(date, name);
            }
        });

        if (result.unavailable()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Holidays-Unavailable", "true");
            return ResponseEntity.ok().headers(headers).body(filteredHolidays);
        }
        return ResponseEntity.ok(filteredHolidays);
    }
}
