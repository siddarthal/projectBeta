package com.recommendationengine.service;


import com.recommendationengine.entity.AttractionRecommendation;
import com.recommendationengine.entity.RecommendationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationEngineService {

    @Autowired
    private OpenAiChatClient chatClient;

    @Autowired
    private AttractionService attractionService;

    private final ObjectMapper objectMapper;

    public RecommendationEngineService() {
        this.objectMapper = new ObjectMapper();
    }

    public List<AttractionRecommendation> getRecommendations(RecommendationRequest request) {
        try {
            // Get attractions along the route
            List<Attraction> nearbyAttractions = attractionService.findAttractionsAlongRoute(
                    request.getSource(),
                    request.getDestination()
            );

            // Filter by category if specified
            if (request.getCategory() != null && !request.getCategory().isEmpty()) {
                nearbyAttractions = nearbyAttractions.stream()
                        .filter(attraction -> attraction.getCategory().equalsIgnoreCase(request.getCategory()))
                        .toList();
            }

            // Use AI to rank and recommend attractions
            String prompt = buildPrompt(nearbyAttractions, request.getUserPreferences());
            String aiResponse = chatClient.call(new Prompt(prompt)).getResult().getOutput().getContent();

            return parseAiResponse(aiResponse, request.getMaxAttractions());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate recommendations", e);
        }
    }

    private String buildPrompt(List<Attraction> attractions, List<String> userPreferences) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Based on the following user preferences: ");
        promptBuilder.append(String.join(", ", userPreferences)).append("\n");
        promptBuilder.append("Please rank and recommend the following attractions:\n");

        for (Attraction attraction : attractions) {
            promptBuilder.append("- ").append(attraction.getName())
                    .append(" (").append(attraction.getCategory()).append(")")
                    .append(": ").append(attraction.getDescription()).append("\n");
        }

        promptBuilder.append("\nProvide your recommendations in JSON format with the following structure:\n");
        promptBuilder.append("[{\"attractionName\": \"name\", \"relevanceScore\": 0.95, ");
        promptBuilder.append("\"recommendationReason\": \"reason why this is recommended\"}]");

        return promptBuilder.toString();
    }

    private List<AttractionRecommendation> parseAiResponse(String aiResponse, int maxAttractions) {
        try {
            List<AttractionRecommendation> recommendations = new ArrayList<>();

            // Extract JSON from the AI response
            int jsonStart = aiResponse.indexOf("[");
            int jsonEnd = aiResponse.lastIndexOf("]") + 1;
            if (jsonStart != -1 && jsonEnd != -1) {
                String jsonContent = aiResponse.substring(jsonStart, jsonEnd);
                List<Object> parsedRecommendations = objectMapper.readValue(jsonContent, List.class);

                for (Object item : parsedRecommendations) {
                    AttractionRecommendation recommendation = objectMapper.convertValue(item, AttractionRecommendation.class);
                    recommendations.add(recommendation);

                    if (recommendations.size() >= maxAttractions) {
                        break;
                    }
                }
            }

            return recommendations;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}