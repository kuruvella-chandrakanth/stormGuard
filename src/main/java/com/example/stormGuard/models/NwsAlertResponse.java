package com.example.stormGuard.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class NwsAlertResponse {

    private String title;
    private List<Feature> features;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private AlertProperties properties;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlertProperties {
        private String event;
        private String severity;
        private String urgency;
        private String certainty;
        private String headline;
        private String description;
        private String instruction;
        private String areaDesc;
        private String onset;
        private String ends;
        private String senderName;
        private List<String> affectedZones;
    }
}
