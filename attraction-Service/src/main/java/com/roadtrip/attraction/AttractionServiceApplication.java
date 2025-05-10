package com.roadtrip.attraction;

import com.roadtrip.attraction.dto.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AttractionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttractionServiceApplication.class, args);
	}

	@FeignClient(name = "USER-SERVICE")
	public interface UserServiceClient {
		@GetMapping("/api/user-service/{userId}")
		User getUserById(@PathVariable("userId") String userId);
	}

}
