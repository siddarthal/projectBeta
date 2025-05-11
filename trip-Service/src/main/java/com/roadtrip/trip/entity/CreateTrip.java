package com.roadtrip.trip.entity;

import com.roadtrip.trip.dto.AttractionRecommendation;
import lombok.Data;

import java.util.List;

@Data
public class CreateTrip {
    private Trip trip;
    private List<AttractionRecommendation> recommendations;

}
