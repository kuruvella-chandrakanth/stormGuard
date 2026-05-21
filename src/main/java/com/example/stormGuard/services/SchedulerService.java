package com.example.stormGuard.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SchedulerService {
    @Autowired
    Map<String,ChatClient> chatClientMap;
    @Autowired
    ToolService toolService;

    @Scheduled(cron = "0 */5 * * * *")
    public void sendingScheduledUpdatesToUsers(){
        ChatClient chatClient=chatClientMap.get("gemini");
        String userMessage="Check any weather/storm alerts present for the subscribed users and post that to users over email to users having subject: Storm guard Tool Digest Email and body with weather/storm alert information, FYI: please dont forget to send email";
        String result=chatClient.prompt().tools(toolService).user(userMessage).system("please include email address of users as well in the response").call().content();
        log.info("Scheduled Email notification to user, response from AI: {}",result);
    }
}
