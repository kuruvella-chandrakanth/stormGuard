package com.example.stormGuard.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherResponse {
    private double latitude;
    private double longitude;
    @JsonAlias(value="timezone")
    private String timeZone;
    @JsonAlias(value="current_weather_units")
    private WeatherInfo weatherUnits;
    @JsonAlias(value="current_weather")
    private WeatherInfo currentWeather;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WeatherInfo{

        private String time;
        private String interval;
        private String temperature;

        @JsonAlias(value="windspeed")
        private String windSpeed;

        @JsonAlias(value="winddirection")
        private String windDirection;
    }

}
