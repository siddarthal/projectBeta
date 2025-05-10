package com.recommendationengine.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Map;
@Data
@Document(collection = "attractions")
public class Attraction {
    @Id
    private String id;
    private String name;
    private Map<String, Double> location; // lat, lng
    private String category;
    private int timeRequired;
    private String description;
}