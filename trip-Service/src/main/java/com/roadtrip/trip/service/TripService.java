package com.roadtrip.trip.service;

import com.roadtrip.trip.entity.Trip;

import java.util.List;
import java.util.Map;

public interface TripService {
    public Trip createTrip(Trip trip);

    public Trip getTripById(String id);

    public List<Trip> getAllTripsListOfUser(String userId);

    public Trip updateStops(List<Map<String, Object>> stops, String id);
}
