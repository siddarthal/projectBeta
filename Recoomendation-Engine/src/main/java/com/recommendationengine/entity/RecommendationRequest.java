package com.recommendationengine.entity;


import lombok.Data;
import java.util.List;

@Data
public class RecommendationRequest {
    private String source;
    private String destination;
    private List<String> userPreferences;
    private String category;
    private int maxAttractions;
    private int maxDuration; // in hours
}