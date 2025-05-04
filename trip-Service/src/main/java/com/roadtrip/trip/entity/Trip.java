package com.roadtrip.trip.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "trips")
@Data
public class Trip {
    @Id
    private String id;
    private String userId;
    private String source;
    private String destination;
    private int duration;
    private List<Map<String, Object>> stops;
}