package com.example.stormGuard.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String user_id;
    private String userName;
    private String email;
    private String state;
    private String country;
    private String city;
    private double latitude;
    private double longitude;

}
