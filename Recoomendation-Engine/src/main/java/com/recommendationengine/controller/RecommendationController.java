package com.recommendationengine.controller;



import com.recommendationengine.entity.AttractionRecommendation;
import com.recommendationengine.entity.RecommendationRequest;
import com.recommendationengine.service.RecommendationEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private RecommendationEngineService recommendationEngine;

    @PostMapping
    public ResponseEntity<List<AttractionRecommendation>> getRecommendations(
            @RequestBody RecommendationRequest request) {
        try {
            List<AttractionRecommendation> recommendations = recommendationEngine.getRecommendations(request);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}