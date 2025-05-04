package com.roadtrip.trip.controller;

import com.roadtrip.trip.dto.ResponseBean;
import com.roadtrip.trip.entity.Trip;
import com.roadtrip.trip.service.TripServiceImpl;
import com.roadtrip.trip.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/trip-service")
public class TripController {

    @Autowired
    TripServiceImpl service;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/createTrip")
    public ResponseEntity<ResponseBean<Trip>> saveTrip(@RequestHeader("Authorization") String token, @RequestBody Trip trip) {

        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        trip.setUserId(email);
        Trip saveTrip = service.createTrip(trip);
        return ResponseEntity.ok(ResponseBean.success("Trip Saved successfully", trip));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBean<Trip>> getTripById(@PathVariable String id) {
        Trip getTripById = service.getTripById(id);
        return ResponseEntity.ok(ResponseBean.success(getTripById));
    }

    @GetMapping("/getByUserID")
    public ResponseEntity<ResponseBean<List<Trip>>> getTripByUserId(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        List<Trip> getTripById = service.getAllTripsListOfUser(email);
        return ResponseEntity.ok(ResponseBean.success(getTripById));
    }

    @PatchMapping("/updateStops")
    public ResponseEntity<ResponseBean<Trip>> updateStops(@RequestBody List<Map<String, Object>> stops, @RequestBody String id) {
        Trip saveTripWithStops = service.updateStops(stops, id);
        return ResponseEntity.ok(ResponseBean.success("Updated Stops successful", saveTripWithStops));
    }
}
