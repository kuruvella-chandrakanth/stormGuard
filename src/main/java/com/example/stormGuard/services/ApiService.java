package com.example.stormGuard.services;

import com.example.stormGuard.helper.GeocodeHelper;
import com.example.stormGuard.helper.StormGuardHelper;
import com.example.stormGuard.helper.WeatherHelper;
import com.example.stormGuard.models.GeoCodeResponse;
import com.example.stormGuard.models.NwsAlertResponse;
import com.example.stormGuard.models.User;
import com.example.stormGuard.models.WeatherResponse;
import com.example.stormGuard.repositories.UserRepository;
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

    @Autowired
    public UserRepository userRepository;

    public NwsAlertResponse getStateAlert(String state){
        return stormGuardHelper.getAlerts(state);
    }

    public NwsAlertResponse getStateAlert(Double latitude,Double longitude){
        return stormGuardHelper.getAlerts(latitude,longitude);
    }

    public GeoCodeResponse getLatLongData(String city){
        return geocodeHelper.getLatLongDetails(city);
    }

    public WeatherResponse getWeatherData(Double latitude, Double longitude){
        return weatherHelper.getWeatherDetails(latitude,longitude);
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User getUser(String email){
        return userRepository.findByEmail(email);
    }

}
