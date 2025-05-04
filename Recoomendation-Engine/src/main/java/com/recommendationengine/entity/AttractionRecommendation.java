package com.recommendationengine.entity;


import lombok.Data;

@Data
public class AttractionRecommendation {
    private String attractionId;
    private String attractionName;
    private String category;
    private String location;
    private String recommendationReason;
    private double relevanceScore;
    private String description;
    private int timeRequired; // in minutes
}