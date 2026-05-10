package com.example.stormGuard.services;

import com.example.stormGuard.helper.GeocodeHelper;
import com.example.stormGuard.helper.StormGuardHelper;
import com.example.stormGuard.helper.WeatherHelper;
import com.example.stormGuard.models.GeoCodeResponse;
import com.example.stormGuard.models.NwsAlertResponse;
import com.example.stormGuard.models.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiService {
    @Autowired
    public StormGuardHelper stormGuardHelper;

    @Autowired
    public GeocodeHelper geocodeHelper;

    @Autowired
    public WeatherHelper weatherHelper;

    public NwsAlertResponse getStateAlert(String state){
        return stormGuardHelper.getAlerts(state);
    }

    public GeoCodeResponse getLatLongData(String city){
        return geocodeHelper.getLatLongDetails(city);
    }

    public WeatherResponse getWeatherData(Double latitude, Double longitude){
        return weatherHelper.getWeatherDetails(latitude,longitude);
    }
}
