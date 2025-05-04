package com.roadtrip.trip.repo;

import com.roadtrip.trip.entity.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends MongoRepository<Trip,String> {
    Optional<List<Trip>> findTripByUserId (String id);
}
