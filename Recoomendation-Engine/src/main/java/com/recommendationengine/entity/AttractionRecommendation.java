package com.recommendationengine.entity;


import lombok.Data;

import java.util.List;

@Data
public class AttractionRecommendation {

    private List<AttractionDetails> attractions;
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
        //these are additional data above already exists in Requests
        private String timeNeededTospent;
        private String description;
    }

}