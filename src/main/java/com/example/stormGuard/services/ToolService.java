package com.example.stormGuard.services;

import com.example.stormGuard.helper.GeocodeHelper;
import com.example.stormGuard.helper.StormGuardHelper;
import com.example.stormGuard.helper.WeatherHelper;
import com.example.stormGuard.models.GeoCodeResponse;
import com.example.stormGuard.models.NwsAlertResponse;
import com.example.stormGuard.models.NwsAlertResponse.AlertProperties;
import com.example.stormGuard.models.WeatherResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ToolService {
    @Autowired
    public StormGuardHelper stormGuardHelper;
    @Autowired
    public GeocodeHelper geocodeHelper;
    @Autowired
    public WeatherHelper weatherHelper;


    @Tool(description = "returns whether any storm or weather alerts are present in a US state. Pass the 2-letter state code like CA, TX, FL etc.")
    public String getAlerts(String state){
        NwsAlertResponse response = stormGuardHelper.getAlerts(state);

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

}
