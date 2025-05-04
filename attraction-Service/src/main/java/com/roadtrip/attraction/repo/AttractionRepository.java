package com.roadtrip.attraction.repo;

import com.roadtrip.attraction.entity.Attraction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttractionRepository extends MongoRepository<Attraction, String> {
    List<Attraction> findByCategory(String category);

    List<Attraction> findByLocation(String location);

    List<Attraction> findByCategoryAndLocation(String category, String location);

}
