package com.example.stormGuard.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bedrock_models")
@Data
public class BedrockModel {
    @Id
    private String id;
    private String displayName;
    private String modelId;
    private String provider;
}
