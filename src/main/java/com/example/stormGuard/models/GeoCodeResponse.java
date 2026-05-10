package com.example.stormGuard.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GeoCodeResponse {
    private List<CityInfo> results;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class CityInfo{
        private int id;
        private String name;
        private String timeZone;

        @JsonAlias("country_code")
        private String countryCode;
        private double latitude;
        private double longitude;
        private String country;

        @JsonAlias("admin1")
        private String state;
        @JsonAlias("admin2")
        private String district;


    }

}
