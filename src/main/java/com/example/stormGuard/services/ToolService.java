package com.example.stormGuard.services;

import com.example.stormGuard.helper.EmailHelper;
import com.example.stormGuard.helper.GeocodeHelper;
import com.example.stormGuard.helper.StormGuardHelper;
import com.example.stormGuard.helper.WeatherHelper;
import com.example.stormGuard.models.GeoCodeResponse;
import com.example.stormGuard.models.NwsAlertResponse;
import com.example.stormGuard.models.NwsAlertResponse.AlertProperties;
import com.example.stormGuard.models.User;
import com.example.stormGuard.models.WeatherResponse;
import com.example.stormGuard.repositories.UserRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    @Autowired
    public EmailHelper emailHelper;


    @Tool(description = "returns whether any storm or weather alerts are present in a US state. Pass the 2-letter state code like CA, TX, FL etc,")
    public String getStormAlerts(String state){
        try {
            NwsAlertResponse response = restClient.get()
                    .uri(nwsBaseUrl + "?area={state}", state)
                    .header("User-Agent", "StormGuard App")
                    .retrieve()
                    .body(NwsAlertResponse.class);

            if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
                return "{\"error\": \"No active weather alerts for state: " + state + "\"}";
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
        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch storm alerts: " + e.getMessage() + "\"}";
        }
    }

    @Tool(description = "returns whether any storm/weather alerts are present for a specific town/city/place in US. Pass the latitude and longitude of the place")
    public String getAlerts(Double latitude,Double longitude){
        try {
            NwsAlertResponse response = stormGuardHelper.getAlerts(latitude,longitude);

            if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
                return "{\"error\": \"No active weather alerts for place: " + latitude + "," + longitude + "\"}";
            }

            List<NwsAlertResponse.Feature> features = response.getFeatures();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Found %d active alert(s) for %s,%s:\n\n", features.size(), latitude, longitude));

            for (int i = 0; i < features.size(); i++) {
                AlertProperties props = features.get(i).getProperties();
                sb.append(String.format("Alert %d:\n", i + 1));
                sb.append(String.format("  Event: %s\n", props.getEvent()));
                sb.append(String.format("  Severity: %s\n", props.getSeverity()));
                sb.append(String.format("  Urgency: %s\n", props.getUrgency()));
                sb.append(String.format("  Area: %s\n", props.getAreaDesc()));
                sb.append(String.format("  Headline: %s\n", props.getHeadline()));
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
        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch alerts: " + e.getMessage() + "\"}";
        }
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
    private List<User> getSubscribedUsers(){
        return userRepository.findAll();
    }

    @Tool(description = "This is used for sending email to a user. Parameters: to (required) - recipient email address, subject (required) - email subject line, body (required) - email body content, ccUser (optional) - CC email address")
    public String sendEmailUser(String to, String subject, String body, String ccUser){
        try {
            if (to == null || to.trim().isEmpty()) {
                return "{\"error\": \"Recipient email (to) is required\"}";
            }
            if (subject == null || subject.trim().isEmpty()) {
                return "{\"error\": \"Email subject is required\"}";
            }
            if (body == null || body.trim().isEmpty()) {
                return "{\"error\": \"Email body is required\"}";
            }
            emailHelper.sendEmailToUser(to, subject, body, ccUser);
            return "{\"success\": \"Email sent successfully to: " + to + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Failed to send email: " + e.getMessage() + "\"}";
        }
    }

}
