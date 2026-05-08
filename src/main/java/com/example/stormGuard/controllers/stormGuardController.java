package com.example.stormGuard.controllers;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class stormGuardController {

    @Autowired
    private Map<String, ChatClient> chatClientMap;

    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthMethod(){
        return ResponseEntity.ok("system is healthy");
    }

    @GetMapping("/stormData")
    public ResponseEntity<String> firstMethod(
            @RequestParam(name="prompt") String userPrompt,
            @RequestParam(name="model", defaultValue="gemini") String model){
        try {
            ChatClient chatClient = chatClientMap.get(model);
            if (chatClient == null) {
                return ResponseEntity.badRequest().body("Unknown model: " + model + ". Available: " + chatClientMap.keySet());
            }
            String result=chatClient
                    .prompt()
                    .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                    .user(userPrompt)
                    .call()
                    .content();

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

}
