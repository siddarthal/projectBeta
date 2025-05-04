package com.roadtrip.attraction.cofig;

import com.roadtrip.attraction.util.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    JwtRequestFilter requestFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("api/trips-service").permitAll()
                                .anyRequest().authenticated()
                ).addFilterBefore(requestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
