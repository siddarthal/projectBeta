package com.roadtrip.trip.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteOptimizationRequest {
    private String source;
    private String destination;
    private List<String> waypoints;
    private int maxDuration; // in hours
    private List<String> preferences; // user preferences for the trip
}