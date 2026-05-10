package com.example.stormGuard.helper;

import com.example.stormGuard.models.GeoCodeResponse;
import com.example.stormGuard.models.NwsAlertResponse;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeocodeHelper {
    @Value("${geocode.base.url}")
    private String geoCodeApi;
    private final RestClient restClient = RestClient.create();

    public GeoCodeResponse getLatLongDetails(String city){
        GeoCodeResponse response = restClient.get()
                .uri(geoCodeApi + "?name={city}", city)
                .retrieve()
                .body(GeoCodeResponse.class);
        return response;
    }
}

