package com.routeoptimizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.routeoptimizer.entity.RouteOptimizationRequest;
import com.routeoptimizer.entity.TripRouteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class RouteOptimizerService {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    public RouteOptimizerService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newFixedThreadPool(10); // Pool size can be adjusted
    }

    public TripRouteResponse optimizeRoute(RouteOptimizationRequest request) {
        try {
            String url = buildDirectionsApiUrl(request.getSource(), request.getDestination(), request.getWaypoints(), request.getPreferences());

            // Set up HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                TripRouteResponse routeResponse = parseDirectionsResponse(response.getBody(), request);

                // Enhance route with attractions if scenic route is preferred
                if (request.getPreferences() != null && request.getPreferences().contains("scenic")) {
                    enhanceRouteWithAttractions(routeResponse);
                }

                return routeResponse;
            } else {
                throw new RuntimeException("Failed to get directions: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to optimize route: " + e.getMessage(), e);
        }
    }

    private void enhanceRouteWithAttractions(TripRouteResponse routeResponse) {
        try {
            List<TripRouteResponse.AttractionDetails> allAttractions = new ArrayList<>();

            // Initialize attractions list in response
            if (routeResponse.getAttractions() == null) {
                routeResponse.setAttractions(new ArrayList<>());
            }

            // Try both segment-based and stop-based approaches for finding attractions

            // 1. Search around each stop directly (more reliable)
            for (TripRouteResponse.StopDetails stop : routeResponse.getStops()) {
                // Search with a reasonable radius around each stop
                List<TripRouteResponse.AttractionDetails> stopAttractions = findNearbyAttractions(
                        stop.getLatitude(), stop.getLongitude(), 5000);

                // Add location context to each attraction
                for (TripRouteResponse.AttractionDetails attraction : stopAttractions) {
                    attraction.setCategory("Near " + stop.getName() + " (" + stop.getAddress() + ")");
                }

                allAttractions.addAll(stopAttractions);
            }

            // 2. Search along route segments as a backup approach
            if (allAttractions.isEmpty()) {
                for (int i = 0; i < routeResponse.getStops().size() - 1; i++) {
                    TripRouteResponse.StopDetails currentStop = routeResponse.getStops().get(i);
                    TripRouteResponse.StopDetails nextStop = routeResponse.getStops().get(i + 1);

                    // Try multiple points along the segment to increase coverage
                    for (double fraction : new double[]{0.25, 0.5, 0.75}) {
                        double midLat = currentStop.getLatitude() +
                                (nextStop.getLatitude() - currentStop.getLatitude()) * fraction;
                        double midLng = currentStop.getLongitude() +
                                (nextStop.getLongitude() - currentStop.getLongitude()) * fraction;

                        // Calculate distance between points
                        double distance = calculateDistance(
                                currentStop.getLatitude(), currentStop.getLongitude(),
                                nextStop.getLatitude(), nextStop.getLongitude());

                        // Use 20% of the distance as search radius, with minimum 2000m and max 10000m
                        int radius = Math.max(2000, Math.min((int)(distance * 0.2), 10000));

                        List<TripRouteResponse.AttractionDetails> segmentAttractions =
                                findNearbyAttractions(midLat, midLng, radius);

                        // Add segment context to each attraction
                        for (TripRouteResponse.AttractionDetails attraction : segmentAttractions) {
                            attraction.setCategory("Between " + currentStop.getName() + " and " + nextStop.getName());
                        }

                        allAttractions.addAll(segmentAttractions);
                    }
                }
            }

            // 3. Try a broader search type if we still have no attractions
            if (allAttractions.isEmpty()) {
                for (TripRouteResponse.StopDetails stop : routeResponse.getStops()) {
                    // Try with multiple place types
                    List<String> placeTypes = List.of("tourist_attraction", "point_of_interest",
                            "natural_feature", "park", "museum");

                    for (String placeType : placeTypes) {
                        List<TripRouteResponse.AttractionDetails> typeAttractions =
                                findNearbyAttractionsWithType(stop.getLatitude(), stop.getLongitude(), 8000, placeType);

                        for (TripRouteResponse.AttractionDetails attraction : typeAttractions) {
                            attraction.setCategory(placeType.replace("_", " ") +
                                    " near " + stop.getName());
                        }

                        allAttractions.addAll(typeAttractions);

                        // If we found attractions with this type, move on to the next stop
                        if (!typeAttractions.isEmpty()) {
                            break;
                        }
                    }
                }
            }

            // Log some debug information
            System.out.println("Found " + allAttractions.size() + " attractions along the route");

            // Set the found attractions
            routeResponse.setAttractions(allAttractions);

        } catch (Exception e) {
            // Log error but don't fail the entire request
            System.err.println("Failed to enhance route with attractions: " + e.getMessage());
            e.printStackTrace();

            // Ensure we at least have an empty list rather than null
            if (routeResponse.getAttractions() == null) {
                routeResponse.setAttractions(new ArrayList<>());
            }
        }
    }

    // Haversine formula to calculate distance between two points on Earth
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in meters
    }

    private List<TripRouteResponse.AttractionDetails> findNearbyAttractions(double latitude, double longitude, int radius) {
        return findNearbyAttractionsWithType(latitude, longitude, radius, "tourist_attraction");
    }

    private List<TripRouteResponse.AttractionDetails> findNearbyAttractionsWithType(
            double latitude, double longitude, int radius, String placeType) {
        try {
            // Log the request being made for debugging
            System.out.println("Searching for " + placeType + " at " + latitude + "," + longitude +
                    " with radius " + radius + "m");

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                    .queryParam("location", latitude + "," + longitude)
                    .queryParam("radius", radius)
                    .queryParam("type", placeType)
                    .queryParam("key", googleMapsApiKey)
                    .queryParam("language", "en") // Ensure consistent language results
                    .queryParam("rankby", "prominence") // Default ranking by prominence
                    .build(false)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                List<TripRouteResponse.AttractionDetails> attractions = parseAttractionsResponse(response.getBody());
                System.out.println("Found " + attractions.size() + " " + placeType + " attractions");
                return attractions;
            } else {
                System.err.println("Failed API response: " + response.getStatusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error finding " + placeType + " attractions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
    }

    private List<TripRouteResponse.AttractionDetails> parseAttractionsResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        List<TripRouteResponse.AttractionDetails> attractions = new ArrayList<>();

        if (root.has("status")) {
            String status = root.get("status").asText();

            // Log the API status for debugging
            System.out.println("Places API response status: " + status);

            if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
                System.err.println("Google Places API error: " + status);
                if (root.has("error_message")) {
                    System.err.println("Error message: " + root.get("error_message").asText());
                }
                // Return empty list for error cases
                return attractions;
            }
        }

        if (root.has("results") && root.get("results").isArray()) {
            JsonNode results = root.get("results");

            // Log how many results we got
            System.out.println("Found " + results.size() + " potential attractions");

            for (JsonNode place : results) {
                // Limit to top 5 attractions per request
                if (attractions.size() >= 5) break;

                try {
                    TripRouteResponse.AttractionDetails attraction = new TripRouteResponse.AttractionDetails();

                    // Name is required
                    if (place.has("name")) {
                        attraction.setName(place.get("name").asText());
                    } else {
                        continue; // Skip places without names
                    }

                    // Address (vicinity or formatted_address)
                    if (place.has("vicinity")) {
                        attraction.setAddress(place.get("vicinity").asText());
                    } else if (place.has("formatted_address")) {
                        attraction.setAddress(place.get("formatted_address").asText());
                    } else {
                        attraction.setAddress("Address not available");
                    }

                    // Location data
                    if (place.has("geometry") && place.get("geometry").has("location")) {
                        JsonNode location = place.get("geometry").get("location");
                        attraction.setLatitude(location.get("lat").asDouble());
                        attraction.setLongitude(location.get("lng").asDouble());
                    } else {
                        // Skip places without location data
                        continue;
                    }

                    // Rating and reviews
                    attraction.setRating(place.has("rating") ? place.get("rating").asDouble() : 0.0);
                    attraction.setTotalRatings(place.has("user_ratings_total") ?
                            place.get("user_ratings_total").asInt() : 0);

                    // Photo reference
                    if (place.has("photos") && place.get("photos").isArray() && place.get("photos").size() > 0) {
                        JsonNode photo = place.get("photos").get(0);
                        if (photo.has("photo_reference")) {
                            String photoRef = photo.get("photo_reference").asText();
                            attraction.setPhotoReference(photoRef);
                        }
                    }

                    // Extract place type information for better categorization
                    if (place.has("types") && place.get("types").isArray() && place.get("types").size() > 0) {
                        JsonNode types = place.get("types");
                        StringBuilder typeInfo = new StringBuilder();
                        for (int i = 0; i < Math.min(3, types.size()); i++) {
                            String type = types.get(i).asText();
                            // Skip generic types
                            if (type.equals("point_of_interest") || type.equals("establishment")) {
                                continue;
                            }

                            if (typeInfo.length() > 0) {
                                typeInfo.append(", ");
                            }
                            // Format the type for display
                            typeInfo.append(type.replace("_", " "));
                        }

                        // Only set if we have meaningful types
                        if (typeInfo.length() > 0) {
                            if (attraction.getCategory() == null) {
                                attraction.setCategory(typeInfo.toString());
                            } else {
                                attraction.setCategory(attraction.getCategory() + " - " + typeInfo.toString());
                            }
                        }
                    }

                    // Add to the list if it passes all checks
                    attractions.add(attraction);

                } catch (Exception e) {
                    // Skip this attraction if something goes wrong
                    System.err.println("Error parsing attraction: " + e.getMessage());
                }
            }
        }

        return attractions;
    }

    private String buildDirectionsApiUrl(String source, String destination, List<String> waypoints, List<String> preferences) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", source)
                .queryParam("destination", destination)
                .queryParam("key", googleMapsApiKey);

        if (waypoints != null && !waypoints.isEmpty()) {
            // Fix the waypoints formatting - use comma instead of pipe for optimize:true
            String waypointsParam = String.join("|", waypoints);
            builder.queryParam("waypoints", "optimize:true|" + waypointsParam);
        }

        // Add user preferences
        if (preferences != null) {
            if (preferences.contains("avoidTolls")) {
                builder.queryParam("avoid", "tolls");
            }

            if (preferences.contains("scenic")) {
                // For scenic routes, we could use alternatives=true to get more route options
                builder.queryParam("alternatives", "true");
            }
        }

        return builder.build(false).toUriString(); // Use build(false) to properly encode the URL
    }

    private TripRouteResponse parseDirectionsResponse(String responseBody, RouteOptimizationRequest request) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        // Check for API error status
        if (root.has("status")) {
            String status = root.get("status").asText();
            if (!"OK".equals(status)) {
                String errorMessage = "Google Maps API error: " + status;
                if (root.has("error_message")) {
                    errorMessage += " - " + root.get("error_message").asText();
                }
                throw new RuntimeException(errorMessage);
            }
        }

        JsonNode routes = root.get("routes");

        if (routes.isArray() && routes.size() > 0) {
            JsonNode route = routes.get(0);
            TripRouteResponse response = new TripRouteResponse();

            // Parse total distance and duration
            JsonNode legs = route.get("legs");
            long totalDistance = 0;
            long totalDuration = 0;
            List<TripRouteResponse.StopDetails> stops = new ArrayList<>();

            // Add source as first stop
            TripRouteResponse.StopDetails sourceStop = new TripRouteResponse.StopDetails();
            sourceStop.setName("Start");
            sourceStop.setAddress(request.getSource());
            if (legs.size() > 0) {
                JsonNode firstLeg = legs.get(0);
                if (firstLeg.has("start_location")) {
                    sourceStop.setLatitude(firstLeg.get("start_location").get("lat").asDouble());
                    sourceStop.setLongitude(firstLeg.get("start_location").get("lng").asDouble());
                }
                // Calculate default time allocation (10% of leg duration)
                sourceStop.setTimeAllocation((int)(firstLeg.get("duration").get("value").asLong() * 0.1 / 60)); // Convert to minutes
            }
            stops.add(sourceStop);

            for (int i = 0; i < legs.size(); i++) {
                JsonNode leg = legs.get(i);
                totalDistance += leg.get("distance").get("value").asLong();
                totalDuration += leg.get("duration").get("value").asLong();

                // Add stop details
                TripRouteResponse.StopDetails stop = new TripRouteResponse.StopDetails();

                // Set stop name based on waypoint order if available
                if (i < legs.size() - 1) {
                    // This is a waypoint
                    stop.setName("Waypoint " + (i + 1));
                } else {
                    // This is the final destination
                    stop.setName("Destination");
                }

                stop.setAddress(leg.get("end_address").asText());
                stop.setLatitude(leg.get("end_location").get("lat").asDouble());
                stop.setLongitude(leg.get("end_location").get("lng").asDouble());

                // Calculate time allocation (15% of total trip time for waypoints, 20% for destination)
                int allocationPercentage = (i < legs.size() - 1) ? 15 : 20;
                stop.setTimeAllocation((int)(totalDuration * allocationPercentage / 100 / 60)); // Convert to minutes

                stops.add(stop);
            }

            response.setTotalDistance(totalDistance);
            response.setTotalDuration(totalDuration);
            response.setStops(stops);

            // Parse waypoint order if available
            if (route.has("waypoint_order")) {
                List<Integer> waypointOrder = new ArrayList<>();
                for (JsonNode order : route.get("waypoint_order")) {
                    waypointOrder.add(order.asInt());
                }
                response.setOptimizedWaypoints(waypointOrder);
            }

            // Get polyline
            if (route.has("overview_polyline") && route.get("overview_polyline").has("points")) {
                response.setPolylinePoints(route.get("overview_polyline").get("points").asText());
            }

            return response;
        } else {
            throw new RuntimeException("No routes found");
        }
    }
}