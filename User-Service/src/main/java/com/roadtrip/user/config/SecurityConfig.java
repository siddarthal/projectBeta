package com.roadtrip.user.config;

import com.roadtrip.user.service.UserService;
import com.roadtrip.user.util.JwtUtil;
import  com.roadtrip.user.util.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @Bean
    JwtRequestFilter requestFilter(){
        return new JwtRequestFilter(jwtUtil,userService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup", "/api/login").permitAll()
                        .anyRequest().authenticated()).
                addFilterBefore(requestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}