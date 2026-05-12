package com.example.stormGuard.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jdk.jfr.Description;
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
        @Description(value = "this is nws event that explains about the kind of storm alert, u can use this field for storm guard events/alerts")
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
        @Description(value = "this gives information of the places that are going to have storm alerts")
        private List<String> affectedZones;
    }
}
