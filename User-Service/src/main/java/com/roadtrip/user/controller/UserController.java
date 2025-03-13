package com.roadtrip.user.controller;

import com.roadtrip.user.entity.User;
import com.roadtrip.user.service.UserService;
import com.roadtrip.user.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<User> userSignup(@RequestBody User user) {
        User registeredUser = userService.rgstrUsr(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String token = userService.lgnUsr(email, password);
        return ResponseEntity.ok(token);
    }

    @PatchMapping("/updatePref")
    public ResponseEntity<User> updatePreferences(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> preferences) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User updatedUser = userService.upDtprfrncs(email, preferences);
        return ResponseEntity.ok(updatedUser);
    }
}