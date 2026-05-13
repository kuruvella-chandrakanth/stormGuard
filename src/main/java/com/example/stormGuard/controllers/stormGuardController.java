package com.example.stormGuard.controllers;


import com.example.stormGuard.models.NwsAlertResponse;
import com.example.stormGuard.models.User;
import com.example.stormGuard.services.ApiService;
import com.example.stormGuard.services.ToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class stormGuardController {

    @Autowired
    private Map<String, ChatClient> chatClientMap;

    @Autowired
    private ToolService toolService;

    @Autowired
    private ApiService apiService;

    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthMethod(){
        return ResponseEntity.ok("system is healthy");
    }

    @GetMapping("/stormData")
    public ResponseEntity<String> firstMethod(
            @RequestParam(name="prompt") String userPrompt,
            @RequestParam(name="model", defaultValue="gemini") String model,
            @RequestParam(name="bedrockModel", required=false) String bedrockModel){
        try {
            ChatClient chatClient = chatClientMap.get(model);
            if (chatClient == null) {
                return ResponseEntity.badRequest().body("Unknown model: " + model + ". Available: " + chatClientMap.keySet());
            }
            var promptSpec = chatClient
                    .prompt()
                    .system("Try to give output in nice paragraph and points format ")
                    .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                    .tools(toolService)
                    .user(userPrompt);

            if ("bedrock".equals(model) && bedrockModel != null) {
                promptSpec.options(ChatOptions.builder()
                        .model(bedrockModel));
            }

            String result = promptSpec.call().content();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/enphaseData")
    public ResponseEntity<String> secondMethod(
            @RequestParam(name="prompt") String userPrompt,
            @RequestParam(name="model", defaultValue="gemini") String model){
        try {
            ChatClient chatClient = chatClientMap.get(model);
            if (chatClient == null) {
                return ResponseEntity.badRequest().body("Unknown model: " + model + ". Available: " + chatClientMap.keySet());
            }
            String instructions="one employee name is Chandrakanth and he is from enphase energy, he work under Suman and he is from banglore" +
                    "Dont answer anything other than asking about him or his company"+" if asked other questions tell sorry that u cannot answer it other than about him";
            String result=chatClient.prompt().system(instructions).user(userPrompt).call().content();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/stormData/system")
    public ResponseEntity<Object> getAlertFromCode(@RequestParam String state){
        return ResponseEntity.ok(apiService.getStateAlert(state));
    }

    @GetMapping("/stormDataCoords/system")
    public ResponseEntity<Object> getAlertFromCoords(@RequestParam Double latitude,@RequestParam Double longitude){
        return ResponseEntity.ok(apiService.getStateAlert(latitude,longitude));
    }

    @GetMapping("/geoCodeData/system")
    public ResponseEntity<Object> getLatLongData(@RequestParam String city){
        return ResponseEntity.ok(apiService.getLatLongData(city));
    }

    @GetMapping("/weatherData/system")
    public ResponseEntity<Object> getWeatherData(@RequestParam Double latitude,@RequestParam Double longitude ){
        return ResponseEntity.ok(apiService.getWeatherData(latitude,longitude));
    }

    @PostMapping("/addUser/system")
    public ResponseEntity<Object> addUser(@RequestBody User user){
        return ResponseEntity.ok(apiService.addUser(user));
    }
    @GetMapping("/getUser/system")
    public ResponseEntity<Object> getUser(@RequestParam String email){
        return ResponseEntity.ok(apiService.getUser(email));
    }


}
