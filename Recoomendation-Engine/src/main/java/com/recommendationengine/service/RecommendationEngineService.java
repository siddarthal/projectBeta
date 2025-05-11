package com.recommendationengine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendationengine.entity.AttractionRecommendation;
import com.recommendationengine.entity.RecommendationRequest;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RecommendationEngineService {

    private final Random random = new Random();

    public List<AttractionRecommendation> getRecommendations(RecommendationRequest request) {
        try {
            List<RecommendationRequest.AttractionDetails> attractions = request.getAttractions();
            int maxDuration = request.getMaxDuration();
            List<String> preferences = request.getPreferences();

            // Filter and rank attractions
            List<AttractionRecommendation.AttractionDetails> recommendedAttractions =
                filterAndRankAttractions(attractions, preferences, maxDuration);

            // Create the recommendation response
            AttractionRecommendation recommendation = new AttractionRecommendation();
            recommendation.setAttractions(recommendedAttractions);

            return Collections.singletonList(recommendation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate recommendations: " + e.getMessage(), e);
        }
    }

    private List<AttractionRecommendation.AttractionDetails> filterAndRankAttractions(
            List<RecommendationRequest.AttractionDetails> attractions,
            List<String> preferences,
            int maxDuration) {

        // 1. Filter attractions based on preferences (if provided)
        List<RecommendationRequest.AttractionDetails> preferredAttractions = attractions;
        if (preferences != null && !preferences.isEmpty()) {
            preferredAttractions = attractions.stream()
                    .filter(attraction -> matchesPreferences(attraction, preferences))
                    .collect(Collectors.toList());

            // If no attractions match preferences, use all attractions
            if (preferredAttractions.isEmpty()) {
                preferredAttractions = attractions;
            }
        }

        // 2. Sort by rating (highest first)
        preferredAttractions.sort(Comparator.comparing(RecommendationRequest.AttractionDetails::getRating).reversed());

        // 3. Calculate how many attractions to include based on maxDuration
        // Estimate average time for visiting an attraction (2 hours per attraction)
        int attractionsToKeep = maxDuration > 0 ? maxDuration / 2 : preferredAttractions.size();
        attractionsToKeep = Math.max(1, Math.min(attractionsToKeep, preferredAttractions.size()));

        // 4. Convert and enrich the top attractions
        return preferredAttractions.stream()
                .limit(attractionsToKeep)
                .map(this::enrichAttraction)
                .collect(Collectors.toList());
    }

    private boolean matchesPreferences(RecommendationRequest.AttractionDetails attraction, List<String> preferences) {
        if (attraction.getCategory() == null) {
            return false;
        }

        String category = attraction.getCategory().toLowerCase();
        String name = attraction.getName().toLowerCase();
        String address = attraction.getAddress() != null ? attraction.getAddress().toLowerCase() : "";

        for (String preference : preferences) {
            String lowerPref = preference.toLowerCase();

            // Special handling for 'scenic' preference
            if (lowerPref.equals("scenic")) {
                // Places likely to be scenic based on keywords or high ratings
                if (name.contains("fort") || name.contains("temple") ||
                    name.contains("garden") || name.contains("park") ||
                    name.contains("world") || name.contains("beach") ||
                    name.contains("point") || name.contains("view") ||
                    category.contains("historical") ||
                    (attraction.getRating() >= 4.5 && attraction.getTotalRatings() > 1000)) {
                    return true;
                }
            }
            // Check if the preference is in the category, name or address
            else if (category.contains(lowerPref) || name.contains(lowerPref) || address.contains(lowerPref)) {
                return true;
            }
        }

        return false;
    }

    private AttractionRecommendation.AttractionDetails enrichAttraction(RecommendationRequest.AttractionDetails attraction) {
        AttractionRecommendation.AttractionDetails enriched = new AttractionRecommendation.AttractionDetails();

        // Copy basic properties
        enriched.setName(attraction.getName());
        enriched.setAddress(attraction.getAddress());
        enriched.setLatitude(attraction.getLatitude());
        enriched.setLongitude(attraction.getLongitude());
        enriched.setRating(attraction.getRating());
        enriched.setTotalRatings(attraction.getTotalRatings());
        enriched.setPhotoReference(attraction.getPhotoReference());
        enriched.setCategory(attraction.getCategory());

        // Add time needed estimate
        enriched.setTimeNeededTospent(generateTimeNeeded(attraction));

        // Add description
        enriched.setDescription(generateDescription(attraction));

        return enriched;
    }

    private String generateTimeNeeded(RecommendationRequest.AttractionDetails attraction) {
        // Generate estimated time based on attraction type and rating
        int baseHours = 1;

        // Adjust time based on rating
        if (attraction.getRating() >= 4.5) {
            baseHours += 1; // Add an hour for highly rated attractions
        }

        // Adjust based on number of ratings (popularity)
        if (attraction.getTotalRatings() > 10000) {
            baseHours += 1; // Add an hour for very popular attractions
        }

        // Add some randomness (15 or 30 minutes)
        int extraMinutes = random.nextInt(2) * 15 + 15;

        return String.format("%d hours %d minutes", baseHours, extraMinutes);
    }

    private String generateDescription(RecommendationRequest.AttractionDetails attraction) {
        StringBuilder description = new StringBuilder();
        String name = attraction.getName().toLowerCase();
        String category = attraction.getCategory() != null ? attraction.getCategory().toLowerCase() : "";

        // Base description based on type
        description.append("A popular ");

        if (name.contains("temple") || name.contains("mandir") || name.contains("kovil") ||
            category.contains("temple") || name.contains("koil")) {
            description.append("temple known for its cultural and religious significance. ");
            description.append("Visitors can enjoy the beautiful architecture and peaceful atmosphere. ");
        } else if (name.contains("museum") || category.contains("museum")) {
            description.append("museum showcasing historical artifacts and exhibitions. ");
            description.append("This is a great place to learn about local history and culture. ");
        } else if (name.contains("park") || name.contains("garden") ||
                  category.contains("park") || category.contains("outdoor")) {
            description.append("recreational area offering a relaxing environment for visitors. ");
            description.append("It features scenic landscapes and is perfect for nature lovers. ");
        } else if (name.contains("fort") || category.contains("fort") ||
                  category.contains("historical")) {
            description.append("historical fort with architectural significance. ");
            description.append("It offers panoramic views of the surrounding area and insights into the region's past. ");
        } else if (name.contains("water") || name.contains("beach") ||
                  category.contains("water") || category.contains("beach")) {
            description.append("water attraction perfect for a refreshing experience. ");
            description.append("This scenic spot offers beautiful views and photo opportunities. ");
        } else {
            description.append("attraction that is worth visiting. ");
            description.append("It offers unique experiences for travelers exploring the area. ");
        }

        // Add rating information
        if (attraction.getRating() > 0) {
            description.append(String.format("With a rating of %.1f from %d visitors, ",
                    attraction.getRating(), attraction.getTotalRatings()));

            if (attraction.getRating() >= 4.5) {
                description.append("it's highly recommended by travelers. ");
            } else if (attraction.getRating() >= 4.0) {
                description.append("it's well-rated by most visitors. ");
            } else {
                description.append("it offers a decent experience. ");
            }
        }

        // Add location context if available
        if (attraction.getCategory() != null && !attraction.getCategory().isEmpty()) {
            description.append("This attraction is located ").append(attraction.getCategory()).append(".");
        }

        return description.toString();
    }
}