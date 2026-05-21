package com.example.stormGuard.controllers;


import com.example.stormGuard.models.BedrockModel;
import com.example.stormGuard.models.User;
import com.example.stormGuard.repositories.BedrockModelRepository;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class stormGuardController {

    @Autowired
    private Map<String, ChatClient> chatClientMap;

    @Autowired
    private ToolService toolService;

    @Autowired
    private ApiService apiService;


    @Autowired
    private BedrockModelRepository bedrockModelRepository;

    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthMethod(){
        return ResponseEntity.ok("system is healthy");
    }

    @GetMapping("/emailHealthCheck")
    public ResponseEntity<String> emailHealthCheck(){
        try {
            apiService.testEmailConnection();
            return ResponseEntity.ok("Email configuration is valid and connection successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Email health check failed: " + e.getMessage());
        }
    }

    @GetMapping("/stormData")
    public ResponseEntity<String> firstMethod(
            @RequestParam(name="prompt") String userPrompt,
            @RequestParam(name="model", defaultValue="gemini") String model,
            @RequestParam(name="bedrockModel", required=false) String bedrockModel,
            @RequestHeader(name="userId", required=false) String conversationId){
        try {
            ChatClient chatClient = chatClientMap.get(model);
            if (chatClient == null) {
                return ResponseEntity.badRequest().body("Unknown model: " + model + ". Available: " + chatClientMap.keySet());
            }
            // Use conversationId if provided, otherwise generate one from prompt (not ideal but prevents null error)
            String convId = (conversationId != null && !conversationId.isEmpty()) ? conversationId : "default";
            var promptSpec = chatClient
                    .prompt()
                    .system("You are a helpful assistant. You can answer general questions on any topic using your own knowledge. " +
                            "You also have access to tools for weather data, storm alerts, geocoding, and user subscriptions — use them when the question is about weather or storms. " +
                            "Try to give output in nice paragraph and points format.")
                    .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(convId).build())
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

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendingEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body,@RequestParam(defaultValue = "null") String canSeeEmail){
        try{
            apiService.sendEmail(to,subject,body,canSeeEmail);
            return ResponseEntity.ok("Email request is sent and will be delivered to user in sometime");
        } catch (Exception e) {
           return ResponseEntity.internalServerError().body("Error occurred:"+e.getMessage()+" and "+e.getLocalizedMessage());

        }
    }

    @GetMapping("/bedrockModels")
    public ResponseEntity<Map<String, List<BedrockModel>>> getBedrockModels() {
        Map<String, List<BedrockModel>> grouped = bedrockModelRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(BedrockModel::getProvider));
        return ResponseEntity.ok(grouped);
    }


}
