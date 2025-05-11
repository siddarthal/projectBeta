package com.roadtrip.trip.mapper;

import com.roadtrip.trip.dto.RouteOptimizationRequest;
import com.roadtrip.trip.entity.Trip;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class TripMapper {
    public RouteOptimizationRequest mapRouteOptimizationRequest(Trip trip){
        RouteOptimizationRequest rtoptmstnrqst = new RouteOptimizationRequest();
        if(trip!=null){
            rtoptmstnrqst.setSource(trip.getSource());
            rtoptmstnrqst.setDestination(trip.getDestination());
            rtoptmstnrqst.setMaxDuration(trip.getDuration());
            HashMap<String,List<String>> hm=trip.getStops();
            List<String> wayPoints = hm.getOrDefault("wayPoints", null);
            if(wayPoints!=null){
                rtoptmstnrqst.setWaypoints(wayPoints);
            }
            List<String> preferences = hm.getOrDefault("preferences", null);
            if(preferences!=null){
                rtoptmstnrqst.setPreferences(preferences);
            }
        }
        return rtoptmstnrqst;
    }
}
