package com.example.stormGuard.services;

import com.example.stormGuard.models.*;
import com.example.stormGuard.models.NwsAlertResponse.AlertProperties;
import com.example.stormGuard.helper.GeocodeHelper;
import com.example.stormGuard.helper.StormGuardHelper;
import com.example.stormGuard.helper.WeatherHelper;
import com.example.stormGuard.models.NwsAlertResponse;
import com.example.stormGuard.models.NwsAlertResponse.AlertProperties;
import com.example.stormGuard.repositories.UserRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ToolService {

    @Value("${nws.us.api}")
    private String nwsBaseUrl;

    private final RestClient restClient = RestClient.create();
    @Autowired
    public StormGuardHelper stormGuardHelper;
    @Autowired
    public GeocodeHelper geocodeHelper;
    @Autowired
    public WeatherHelper weatherHelper;
    @Autowired
    public UserRepository userRepository;


    @Tool(description = "returns whether any storm or weather alerts are present in a US state. Pass the 2-letter state code like CA, TX, FL etc,")
    public String getStormAlerts(String state){
        NwsAlertResponse response = restClient.get()
                .uri(nwsBaseUrl + "?area={state}", state)
                .header("User-Agent", "StormGuard App")
                .retrieve()
                .body(NwsAlertResponse.class);

        if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
            return "No active weather alerts for state: " + state;
        }

        List<NwsAlertResponse.Feature> features = response.getFeatures();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %d active alert(s) for %s:\n\n", features.size(), state));

        for (int i = 0; i < features.size(); i++) {
            AlertProperties props = features.get(i).getProperties();
            sb.append(String.format("Alert %d:\n", i + 1));
            sb.append(String.format("  Event: %s\n", props.getEvent()));
            sb.append(String.format("  Severity: %s\n", props.getSeverity()));
            sb.append(String.format("  Urgency: %s\n", props.getUrgency()));
            sb.append(String.format("  Area: %s\n", props.getAreaDesc()));
            sb.append(String.format("  Headline: %s\n", props.getHeadline()));
            sb.append(String.format("  Description: %s\n", props.getDescription()));
            if (props.getInstruction() != null) {
                sb.append(String.format("  Instruction: %s\n", props.getInstruction()));
            }
            sb.append(String.format("  From: %s\n", props.getOnset()));
            sb.append(String.format("  Until: %s\n", props.getEnds()));
            sb.append(String.format("  Source: %s\n", props.getSenderName()));
            if (props.getAffectedZones() != null && !props.getAffectedZones().isEmpty()) {
                sb.append(String.format("  Affected Zones: %s\n", String.join(", ", props.getAffectedZones())));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Tool(description = "returns whether any storm/weather alerts are present for a specific town/city/place in US. Pass the latitude and longitude of the place")
    public String getAlerts(Double latitude,Double longitude){
        NwsAlertResponse response = stormGuardHelper.getAlerts(latitude,longitude);

        if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
            return "No active weather alerts for place: " + latitude+longitude;
        }

        List<NwsAlertResponse.Feature> features = response.getFeatures();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %d active alert(s) for %s,%s:\n\n", features.size(), longitude,longitude));

        for (int i = 0; i < features.size(); i++) {
            AlertProperties props = features.get(i).getProperties();
            sb.append(String.format("Alert %d:\n", i + 1));
            sb.append(String.format("  Event: %s\n", props.getEvent()));
            sb.append(String.format("  Severity: %s\n", props.getSeverity()));
            sb.append(String.format("  Urgency: %s\n", props.getUrgency()));
            sb.append(String.format("  Area: %s\n", props.getAreaDesc()));
            sb.append(String.format("  Headline: %s\n", props.getHeadline()));
//            sb.append(String.format("  Description: %s\n", props.getDescription()));
            if (props.getInstruction() != null) {
                sb.append(String.format("  Instruction: %s\n", props.getInstruction()));
            }
            sb.append(String.format("  From: %s\n", props.getOnset()));
            sb.append(String.format("  Until: %s\n", props.getEnds()));
            sb.append(String.format("  Source: %s\n", props.getSenderName()));
            if (props.getAffectedZones() != null && !props.getAffectedZones().isEmpty()) {
                List<String> zoneCodes = props.getAffectedZones().stream()
                        .map(zone -> zone.contains("/") ? zone.substring(zone.lastIndexOf("/") + 1) : zone)
                        .toList();
                sb.append(String.format("  Affected Zones: %s\n", String.join(", ", zoneCodes)));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Tool(description = "this will fetch the latitude, longitute details of a place")
    public GeoCodeResponse getLatLongDetails(String city){
         return geocodeHelper.getLatLongDetails(city);
    }
    @Tool(description = "this will fetch the weather details of a place")
    public WeatherResponse getWeatherDetails(Double latitude, Double longitude){
        return weatherHelper.getWeatherDetails(latitude,longitude);
    }
    @Tool(description = "list of users who has subscribed to storm alerts, this has information about their city,state,country, lat and long e.t.c")
    public List<User> getSubscribedUsers(){
        return userRepository.findAll();
    }

}
