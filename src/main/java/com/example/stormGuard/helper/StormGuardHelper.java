package com.example.stormGuard.helper;

import com.example.stormGuard.models.NwsAlertResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class StormGuardHelper {

    @Value("${nws.us.api}")
    private String nwsBaseUrl;
    private final RestClient restClient = RestClient.create();

    public NwsAlertResponse getAlerts(String state){
        NwsAlertResponse response = restClient.get()
                .uri(nwsBaseUrl + "?area={state}", state)
                .retrieve()
                .body(NwsAlertResponse.class);

        return response;
    }

    public NwsAlertResponse getAlerts(Double latitude,Double Longitude){
        NwsAlertResponse response = restClient.get()
                .uri(nwsBaseUrl + "?point={latitude},{Longitude}", latitude,Longitude)
                .retrieve()
                .body(NwsAlertResponse.class);

        return response;
    }
}
