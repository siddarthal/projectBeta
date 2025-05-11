package com.roadtrip.trip.dto;

import lombok.Data;

import java.util.List;
@Data
public class TripRouteEnhanceRequest {
    private long totalDistance;
    private long totalDuration;
    private List<Integer> optimizedWaypoints;
    private String polylinePoints;
    private List<StopDetails> stops;
    private List<AttractionDetails> attractions;
    private int maxDuration; // in hours
    private List<String> preferences; // user preferences for the trip

    @Data
    public static class StopDetails {
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private int timeAllocation;
    }

    @Data
    public static class AttractionDetails {
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private double rating;
        private int totalRatings;
        private String photoReference;
        private String category;
    }
}
