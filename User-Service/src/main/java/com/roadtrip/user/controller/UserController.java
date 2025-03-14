package com.roadtrip.user.controller;

import com.roadtrip.user.dto.LoginRequest;
import com.roadtrip.user.dto.ResponseBean;
import com.roadtrip.user.entity.User;
import com.roadtrip.user.service.UserService;
import com.roadtrip.user.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/user-service")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBean<?>> userSignup(@RequestBody User user) {
        User registeredUser = userService.rgstrUsr(user);
        return ResponseEntity.ok(ResponseBean.success("Registered User Successfully", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBean<?>> userLogin(@RequestBody LoginRequest credentials) {
        String token = userService.lgnUsr(credentials);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(ResponseBean.success("login successful"));
    }

    @PatchMapping("/updatePref")
    public ResponseEntity<ResponseBean<?>> updatePreferences(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> preferences) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User updatedUser = userService.upDtprfrncs(email, preferences);
        return ResponseEntity.ok(ResponseBean.success("updated preferences successfully", updatedUser));
    }
}