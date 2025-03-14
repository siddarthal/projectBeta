package com.roadtrip.user.service;

import com.roadtrip.user.dto.LoginRequest;
import com.roadtrip.user.entity.CustomUserDetails;
import com.roadtrip.user.entity.User;

import java.util.Map;


public interface UserService {
    public User rgstrUsr(User user);

    public User upDtprfrncs(String email, Map<String, Object> preferences);

    public String lgnUsr(LoginRequest req);

    public CustomUserDetails loadUserByUsername(String email);
}
