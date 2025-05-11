package com.roadtrip.trip.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;
import java.util.Map;
@Data
@Document(collection = "attractions")
public class Attraction {
    @Id
    private String id;
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
