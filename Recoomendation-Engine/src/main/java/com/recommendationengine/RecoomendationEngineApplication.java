package com.recommendationengine;

import com.recommendationengine.entity.Attraction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RecoomendationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecoomendationEngineApplication.class, args);
	}
	@FeignClient(name = "ATTRACTION-SERVICE")
	public interface AttractionServiceClient {
		@GetMapping("/api/attractions/{id}")
		Attraction getAttractionById(@PathVariable("id") String id);

		@GetMapping("/api/attractions")
		List<Attraction> getAttractionsByLocation(@RequestParam("location") String location);
	}
}
