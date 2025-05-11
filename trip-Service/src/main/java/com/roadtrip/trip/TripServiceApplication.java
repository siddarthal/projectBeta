package com.roadtrip.trip;

import com.roadtrip.trip.dto.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class TripServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripServiceApplication.class, args);
	}
	@FeignClient(name = "ATTRACTION-SERVICE", url = "http://localhost:8083")
	public interface AttractionServiceClient {
//		@GetMapping("/api/attractions/{id}")
//		Attraction getAttractionById(@PathVariable("id") String id);
		@PostMapping("/api/attractions")
		Attraction createAttractionJourney(@RequestHeader("Authorization") String token,@RequestBody List<AttractionRecommendation> request);

//		@GetMapping("/api/attractions")
//		List<Attraction> getAttractionsByLocation(@RequestParam("location") String location);
	}
	@FeignClient(name = "ROUTE-OPTIMIZER", url = "http://localhost:8080")
	public interface RouteOptimizerClient {
		@PostMapping("/api/routes/optimize")
		TripRouteResponse optimizeRoute(@RequestBody RouteOptimizationRequest request);

		@GetMapping("/api/route-optimizer/calculate-time")
		int calculateTime(@RequestParam("source") String source,
						  @RequestParam("destination") String destination);
	}
	@FeignClient(name = "RECOMMENDATION-ENGINE" , url = "http://localhost:8082")
	public interface RecommendationEngine {
		@PostMapping("/api/recommendations")
		List<AttractionRecommendation> enhanceRecommendation(@RequestBody TripRouteEnhanceRequest request);

	}
}
