package com.example.stormGuard.configurations;

import com.example.stormGuard.models.BedrockModel;
import com.example.stormGuard.repositories.BedrockModelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BedrockModelRepository bedrockModelRepository;

    public DataSeeder(BedrockModelRepository bedrockModelRepository) {
        this.bedrockModelRepository = bedrockModelRepository;
    }

    @Override
    public void run(String... args) {
        if (bedrockModelRepository.count() > 0) {
            return;
        }

        List<BedrockModel> models = List.of(
                model("claude-opus-4.7", "us.anthropic.claude-opus-4-7", "Anthropic"),
                model("claude-opus-4.6", "us.anthropic.claude-opus-4-6-v1", "Anthropic"),
                model("claude-opus-4.5", "us.anthropic.claude-opus-4-5-20251101-v1:0", "Anthropic"),
                model("claude-opus-4.1", "us.anthropic.claude-opus-4-1-20250805-v1:0", "Anthropic"),
                model("claude-opus-4", "us.anthropic.claude-opus-4-20250514-v1:0", "Anthropic"),
                model("claude-sonnet-4.6", "us.anthropic.claude-sonnet-4-6", "Anthropic"),
                model("claude-sonnet-4.5", "us.anthropic.claude-sonnet-4-5-20250929-v1:0", "Anthropic"),
                model("claude-sonnet-4", "us.anthropic.claude-sonnet-4-20250514-v1:0", "Anthropic"),
                model("claude-haiku-4.5", "us.anthropic.claude-haiku-4-5-20251001-v1:0", "Anthropic"),
                model("claude-3.5-haiku", "us.anthropic.claude-3-5-haiku-20241022-v1:0", "Anthropic"),
                model("claude-3-haiku", "us.anthropic.claude-3-haiku-20240307-v1:0", "Anthropic"),
                model("claude-3-sonnet", "us.anthropic.claude-3-sonnet-20240229-v1:0", "Anthropic"),

                model("llama-4-maverick", "meta.llama4-maverick-17b-instruct-v1:0", "Meta"),
                model("llama-4-scout", "meta.llama4-scout-17b-instruct-v1:0", "Meta"),
                model("llama-3.3-70b", "meta.llama3-3-70b-instruct-v1:0", "Meta"),
                model("llama-3.2-90b", "meta.llama3-2-90b-instruct-v1:0", "Meta"),
                model("llama-3.2-11b", "meta.llama3-2-11b-instruct-v1:0", "Meta"),
                model("llama-3.2-3b", "meta.llama3-2-3b-instruct-v1:0", "Meta"),
                model("llama-3.2-1b", "meta.llama3-2-1b-instruct-v1:0", "Meta"),
                model("llama-3.1-70b", "meta.llama3-1-70b-instruct-v1:0", "Meta"),
                model("llama-3.1-8b", "meta.llama3-1-8b-instruct-v1:0", "Meta"),
                model("llama-3-70b", "meta.llama3-70b-instruct-v1:0", "Meta"),
                model("llama-3-8b", "meta.llama3-8b-instruct-v1:0", "Meta"),

                model("deepseek-r1", "deepseek.r1-v1:0", "DeepSeek"),
                model("deepseek-v3.2", "deepseek.v3.2", "DeepSeek"),

                model("nova-premier", "amazon.nova-premier-v1:0", "Amazon"),
                model("nova-pro", "amazon.nova-pro-v1:0", "Amazon"),
                model("nova-lite", "amazon.nova-lite-v1:0", "Amazon"),
                model("nova-micro", "amazon.nova-micro-v1:0", "Amazon"),

                model("mistral-large-3", "mistral.mistral-large-3-675b-instruct", "Mistral"),
                model("mistral-large", "mistral.mistral-large-2402-v1:0", "Mistral"),
                model("mistral-small", "mistral.mistral-small-2402-v1:0", "Mistral"),
                model("devstral-2", "mistral.devstral-2-123b", "Mistral"),
                model("magistral-small", "mistral.magistral-small-2509", "Mistral"),
                model("pixtral-large", "mistral.pixtral-large-2502-v1:0", "Mistral"),
                model("mixtral-8x7b", "mistral.mixtral-8x7b-instruct-v0:1", "Mistral")
        );

        bedrockModelRepository.saveAll(models);
        System.out.println("Seeded " + models.size() + " Bedrock models into MongoDB.");
    }

    private BedrockModel model(String displayName, String modelId, String provider) {
        BedrockModel m = new BedrockModel();
        m.setDisplayName(displayName);
        m.setModelId(modelId);
        m.setProvider(provider);
        return m;
    }
}
