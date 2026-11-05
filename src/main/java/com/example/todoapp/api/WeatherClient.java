package com.example.todoapp.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherClient {
    private final RestClient restClient;

    public WeatherClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.create(new RestTemplate(factory));
    }

    public FetchResult fetch(double latitude, double longitude, String from, String to) {
        try {
            String response = restClient.get().uri(uri -> uri.scheme("https").host("api.open-meteo.com")
                    .path("/v1/forecast").queryParam("latitude", latitude).queryParam("longitude", longitude)
                    .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                    .queryParam("start_date", from).queryParam("end_date", to)
                    .queryParam("timezone", "Asia/Tokyo").build()).retrieve().body(String.class);
            List<String> dates = values(response, "time", true);
            List<String> codes = values(response, "weather_code", false);
            List<String> maxTemps = values(response, "temperature_2m_max", false);
            List<String> minTemps = values(response, "temperature_2m_min", false);
            List<String> rainChances = values(response, "precipitation_probability_max", false);
            List<DailyWeather> forecasts = new ArrayList<>();
            for (int i = 0; i < dates.size(); i++) {
                forecasts.add(new DailyWeather(dates.get(i), Integer.parseInt(codes.get(i)),
                        Double.parseDouble(maxTemps.get(i)), Double.parseDouble(minTemps.get(i)),
                        Integer.parseInt(rainChances.get(i))));
            }
            return new FetchResult(forecasts, false);
        } catch (Exception e) {
            return new FetchResult(List.of(), true);
        }
    }

    private static List<String> values(String json, String key, boolean quoted) {
        String marker = "\"" + key + "\":[";
        int start = json.indexOf(marker);
        if (start < 0) throw new IllegalArgumentException("Missing weather field: " + key);
        int end = json.indexOf(']', start + marker.length());
        if (end < 0) throw new IllegalArgumentException("Invalid weather response");
        String content = json.substring(start + marker.length(), end).replace("\"", "");
        List<String> result = new ArrayList<>();
        for (String value : content.split(",")) {
            if (!value.isBlank()) result.add(value.trim());
        }
        return result;
    }

    public static final class DailyWeather {
        private final String date;
        private final int weatherCode;
        private final double maxTemperature;
        private final double minTemperature;
        private final int precipitationProbability;

        public DailyWeather(String date, int weatherCode, double maxTemperature,
                double minTemperature, int precipitationProbability) {
            this.date = date;
            this.weatherCode = weatherCode;
            this.maxTemperature = maxTemperature;
            this.minTemperature = minTemperature;
            this.precipitationProbability = precipitationProbability;
        }

        public String getDate() { return date; }
        public int getWeatherCode() { return weatherCode; }
        public double getMaxTemperature() { return maxTemperature; }
        public double getMinTemperature() { return minTemperature; }
        public int getPrecipitationProbability() { return precipitationProbability; }
    }

    public static final class FetchResult {
        private final List<DailyWeather> forecasts;
        private final boolean unavailable;

        public FetchResult(List<DailyWeather> forecasts, boolean unavailable) {
            this.forecasts = forecasts;
            this.unavailable = unavailable;
        }

        public List<DailyWeather> getForecasts() { return forecasts; }
        public boolean isUnavailable() { return unavailable; }
    }
}
