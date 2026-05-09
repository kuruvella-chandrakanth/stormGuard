package com.example.stormGuard.services;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Tool(description = "returns weather of a place")
    public String getWeather(String city){
        return "city"+city+"temperature 25 degrees";
    }

}
