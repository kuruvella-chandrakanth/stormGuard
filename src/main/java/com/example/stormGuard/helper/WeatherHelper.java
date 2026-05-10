package com.example.stormGuard.helper;

import com.example.stormGuard.models.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherHelper {
    @Value("${weather.base.url}")
    private String weatherApi;
    private final RestClient restClient = RestClient.create();

    public WeatherResponse getWeatherDetails(Double latitude, Double longitude){
        WeatherResponse response = restClient.get()
                .uri(weatherApi + "?latitude={latitude}&longitude={longitude}&current_weather={current_weather}", latitude,longitude,true)
                .retrieve()
                .body(WeatherResponse.class);
        return response;
    }
}
