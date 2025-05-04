package com.roadtrip.attraction.controller;


import com.roadtrip.attraction.dto.ResponseBean;
import com.roadtrip.attraction.entity.Attraction;
import com.roadtrip.attraction.service.AttractionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionServiceImpl attractionService;

    @Autowired
    public AttractionController(AttractionServiceImpl attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public ResponseEntity<ResponseBean<List<Attraction>>> getAllAttractions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location) {

        List<Attraction> attractions;

        if (category != null && location != null) {
            attractions = attractionService.getAttractionsByCategoryAndLocation(category, location);
        } else if (category != null) {
            attractions = attractionService.getAttractionsByCategory(category);
        } else if (location != null) {
            attractions = attractionService.getAttractionsByLocation(location);
        } else {
            attractions = attractionService.getAllAttractions();
        }

        return  ResponseEntity.ok(ResponseBean.success("Data Fetched Successfully", attractions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attraction> getAttractionById(@PathVariable String id) {
        Optional<Attraction> attraction = attractionService.getAttractionById(id);
        return attraction.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping
    public ResponseEntity<Attraction> createAttraction(@RequestBody Attraction attraction) {
        Attraction newAttraction = attractionService.createAttraction(attraction);
        return new ResponseEntity<>(newAttraction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attraction> updateAttraction(@PathVariable String id, @RequestBody Attraction attraction) {
        Optional<Attraction> updatedAttraction = attractionService.updateAttraction(id, attraction);
        return updatedAttraction.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAttraction(@PathVariable String id) {
        boolean deleted = attractionService.deleteAttraction(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}