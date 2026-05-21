package com.example.stormGuard.repositories;

import com.example.stormGuard.models.BedrockModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedrockModelRepository extends MongoRepository<BedrockModel, String> {
    List<BedrockModel> findByProvider(String provider);
}
