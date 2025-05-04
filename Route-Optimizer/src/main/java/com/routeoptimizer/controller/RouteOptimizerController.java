package com.routeoptimizer.controller;

import com.routeoptimizer.entity.RouteOptimizationRequest;
import com.routeoptimizer.entity.TripRouteResponse;
import com.routeoptimizer.service.RouteOptimizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteOptimizerController {

    @Autowired
    private RouteOptimizerService routeOptimizerService;

    @PostMapping("/optimize")
    public ResponseEntity<?> optimizeRoute(@RequestBody RouteOptimizationRequest request) {
        try {
            // Validate request
            if (request.getSource() == null || request.getSource().isEmpty()) {
                return ResponseEntity.badRequest().body("Source location is required");
            }

            if (request.getDestination() == null || request.getDestination().isEmpty()) {
                return ResponseEntity.badRequest().body("Destination location is required");
            }

            TripRouteResponse response = routeOptimizerService.optimizeRoute(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Route optimization failed: " + e.getMessage());
        }
    }

    @GetMapping("/photos/{photoReference}")
    public ResponseEntity<?> getPlacePhoto(@PathVariable String photoReference,
                                           @RequestParam(defaultValue = "400") int maxWidth) {
        try {
            // This endpoint would proxy Google Place Photos API requests
            // Implementing this would require additional service method
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body("Photo retrieval not yet implemented");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve photo: " + e.getMessage());
        }
    }
}