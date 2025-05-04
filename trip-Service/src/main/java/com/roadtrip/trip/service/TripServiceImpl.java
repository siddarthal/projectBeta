package com.roadtrip.trip.service;

import com.roadtrip.trip.entity.Trip;
import com.roadtrip.trip.exception.TripNotFoundException;
import com.roadtrip.trip.exception.TripSaveFailureException;
import com.roadtrip.trip.exception.TripServiceException;
import com.roadtrip.trip.repo.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TripServiceImpl implements TripService {
    private static final Logger log = LoggerFactory.getLogger(TripServiceImpl.class);
    @Autowired
    TripRepository tripRepository;

    @Override
    public Trip createTrip(Trip trip) {
        log.info("Saving Trip for user {} :", trip.getUserId());
        try {
            return tripRepository.save(trip);
        } catch (Exception e) {
            log.error("Unable to Save Trip for user : {} ", trip.getUserId());
            throw new TripSaveFailureException(e.getCause());
        }
    }

    @Override
    public Trip getTripById(String id) {
        log.info("fetching trip details for trip id : {}", id);
        try {
            if (tripRepository.findById(id).isPresent()) {
                log.info("successfully fetched Trip Details with tripId : {}", id);
                return tripRepository.findById(id).get();
            } else {
                String message = "No Trip to Fetch  with TripId: " + id;
                log.error(message);
                throw new TripNotFoundException(message);
            }
        } catch (TripNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TripServiceException("Error occurred while fetching Trip ID :" + id);
        }

    }

    @Override
    public List<Trip> getAllTripsListOfUser(String userId) {
        log.info("fetching all trip details for user id : {}", userId);
        try {
            if (tripRepository.findTripByUserId(userId).isPresent()) {
                log.info("successfully fetched Trip Details with userID : {}", userId);
                return tripRepository.findTripByUserId(userId).get();
            } else {
                String message = "No Trips to Fetch  with UserID: " + userId;
                log.error(message);
                throw new TripNotFoundException(message);
            }
        } catch (TripNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TripServiceException("Error occurred while fetching all Trips with userID :" + userId);
        }
    }


    @Override
    public Trip updateStops(List<Map<String, Object>> stops, String id) {
        log.info("fetching trip details for trip id (updating stops) : {}", id);
        try {
            if (tripRepository.findById(id).isPresent()) {
                Trip exisistingTrip = tripRepository.findById(id).get();
                exisistingTrip.setStops(stops);
                log.info("successfully saved stops for tripID : {}", id);
                return tripRepository.save(exisistingTrip);
            } else {
                String message = "No Trip to Fetch for updating stops  with TripId: " + id;
                log.error(message);
                throw new TripNotFoundException(message);
            }
        } catch (TripNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TripServiceException("Error occurred while fetching TripID for for updating stops with ID:" + id);
        }


    }
}
