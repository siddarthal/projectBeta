package com.roadtrip.attraction.service;

import com.roadtrip.attraction.entity.Attraction;
import com.roadtrip.attraction.exception.ResourceNotFoundException;
import com.roadtrip.attraction.exception.TravelAppException;
import com.roadtrip.attraction.repo.AttractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttractionServiceImpl {
    @Autowired
    AttractionRepository attractionRepository;

    private static final Logger log = LoggerFactory.getLogger(AttractionServiceImpl.class);

    public List<Attraction> getAllAttractions() {
        log.info("fetching attraction Data");
        try {
            List<Attraction> findAll = attractionRepository.findAll();
            if (findAll.isEmpty()) {
                log.error("No Attractions found");
                throw new ResourceNotFoundException("No Attractions found");
            }
            log.info("fetched attraction Data successfully");
            return findAll;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception Occurred while fetching data");
            throw new TravelAppException("Exception Occurred while fetching data", e.getCause());
        }

    }

    public Optional<Attraction> getAttractionById(String id) {
        log.info("fetching attraction Data with id : {}", id);
        try {
            Optional<Attraction> byId = attractionRepository.findById(id);
            if (byId.isEmpty()) {
                throw new ResourceNotFoundException("No Attractions found with id " + id);
            }
            log.info("fetched attraction Data successfully with id : {}", id);
            return byId;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception Occurred while fetching data with id : {}", id);
            throw new TravelAppException("No Attractions found with id " + id, e.getCause());
        }

    }

    public Attraction createAttraction(Attraction attraction) {
        // Validation could be added here or through bean validation
        return attractionRepository.save(attraction);
    }

    public boolean deleteAttraction(String id) {
        try {
            if (attractionRepository.existsById(id)) {
                attractionRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Exception Occurred while data data with id : {}", id);
            throw new TravelAppException("No Attractions found to delete with id " + id, e.getCause());
        }

    }
}
